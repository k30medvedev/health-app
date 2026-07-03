package com.example.lab;

import com.intuit.karate.junit5.Karate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ResourceLock("postgres-container")
class ApiKarateTest extends AbstractIntegrationTest {

	@LocalServerPort
	private int port;

	@BeforeEach
	void exposePortToKarate() {
		System.setProperty("karate.server.port", String.valueOf(port));
	}

	@Karate.Test
	Karate users() {
		return Karate.run("classpath:karate/users.feature");
	}

	@Karate.Test
	Karate analyses() {
		return Karate.run("classpath:karate/analyses.feature");
	}
}
