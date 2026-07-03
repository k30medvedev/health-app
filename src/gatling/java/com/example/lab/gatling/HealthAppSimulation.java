package com.example.lab.gatling;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

/**
 * Load test for the Users/Analyses API. Point it at a running instance with
 * -DbaseUrl=http://host:port (defaults to localhost:8080), e.g.:
 * docker compose up -d app
 * ./gradlew gatlingRun -DbaseUrl=http://localhost:8080
 */
public class HealthAppSimulation extends Simulation {

	private static final String BASE_URL = System.getProperty("baseUrl", "http://localhost:8080");

	private final HttpProtocolBuilder httpProtocol = http
			.baseUrl(BASE_URL)
			.acceptHeader("application/json")
			.contentTypeHeader("application/json");

	private final Iterator<Map<String, Object>> emailFeeder = Stream.generate((java.util.function.Supplier<Map<String, Object>>) () -> {
		Map<String, Object> row = new HashMap<>();
		row.put("email", "gatling-" + UUID.randomUUID() + "@example.com");
		return row;
	}).iterator();

	// Every scenario starts by logging in with the demo credentials and reusing the
	// token as a Bearer header on the requests that follow, since the API now requires it.
	private final ChainBuilder login = exec(http("Login")
			.post("/api/auth/login")
			.body(StringBody("{ \"username\": \"demo\", \"password\": \"demo123\" }"))
			.check(status().is(200))
			.check(jsonPath("$.token").saveAs("token")));

	private final ChainBuilder createUserReadAnalysis = feed(emailFeeder)
			.exec(login)
			.exec(http("Create user")
					.post("/api/users")
					.header("Authorization", "Bearer #{token}")
					.body(StringBody("{ \"fullName\": \"Gatling User\", \"email\": \"#{email}\" }"))
					.check(status().is(201))
					.check(jsonPath("$.id").saveAs("userId")))
			.exec(http("Get user by id")
					.get("/api/users/#{userId}")
					.header("Authorization", "Bearer #{token}")
					.check(status().is(200)))
			.exec(http("Create analysis for user")
					.post("/api/users/#{userId}/analyses")
					.header("Authorization", "Bearer #{token}")
					.body(StringBody("{ \"testName\": \"Hemoglobin\", \"resultValue\": \"145\", "
							+ "\"unit\": \"g/L\", \"referenceRange\": \"130-160\", \"takenAt\": \"2026-01-01\" }"))
					.check(status().is(201))
					.check(jsonPath("$.id").saveAs("analysisId")))
			.exec(http("Get analysis by id")
					.get("/api/analyses/#{analysisId}")
					.header("Authorization", "Bearer #{token}")
					.check(status().is(200)));

	private final ChainBuilder browseUsers = exec(login)
			.exec(http("List users")
					.get("/api/users")
					.header("Authorization", "Bearer #{token}")
					.check(status().is(200)));

	private final ScenarioBuilder createAndReadJourney = scenario("Create user, add analysis, read it back")
			.exec(createUserReadAnalysis);

	private final ScenarioBuilder browseJourney = scenario("Browse users")
			.exec(browseUsers);

	{
		setUp(
				createAndReadJourney.injectOpen(rampUsers(20).during(20)),
				browseJourney.injectOpen(rampUsers(30).during(20))
		)
				.protocols(httpProtocol)
				.assertions(
						global().responseTime().max().lt(2000),
						global().successfulRequests().percent().gt(99.0)
				);
	}
}
