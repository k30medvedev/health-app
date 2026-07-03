# Health App

Health App is a small Spring Boot playground for trying out modern Java tooling, CI security scanning and observability practices. It manages users and the lab analyses attached to them.

## Stack

Java 26, Gradle 9.6, Spring Boot 4.1 (Web MVC, Data JPA, Validation, OpenFeign), H2 in memory database with Flyway migrations, Docker, springdoc OpenAPI, OpenTelemetry tracing, Prometheus metrics, Grafana and Tempo.

## Domain

A user can have many analyses, each analysis belongs to exactly one user.

## API

| Method | Path | Description |
|---|---|---|
| POST | /api/users | Create a user |
| GET | /api/users | List users (paged) |
| GET | /api/users/{id} | Get a user |
| POST | /api/users/{userId}/analyses | Add an analysis for a user |
| GET | /api/users/{userId}/analyses | List a user's analyses |
| GET | /api/analyses/{id} | Get an analysis |
| DELETE | /api/analyses/{id} | Delete an analysis |

OpenAPI docs are served at /swagger-ui.html once the app is running.

## Authentication

Every endpoint under /api except /api/auth/login requires a bearer JWT. This is a stub for demo purposes, not real user management: there is one hardcoded credential, demo / demo123, and POST /api/auth/login hands back a signed token if it matches. Spring Security validates that token as an OAuth2 resource server on every other request. The signing secret lives in application.yml as a lab-only placeholder and would move to an env var or a secrets manager in a real deployment. The Feign demo client (/api/demo/feign/*) authenticates itself the same way through a request interceptor, so it keeps working without any manual token handling.

```
curl -X POST http://localhost:8080/api/auth/login -H "Content-Type: application/json" -d "{\"username\": \"demo\", \"password\": \"demo123\"}"
```

## Trying it out quickly

The file requests.http at the repository root has ready made example requests for every endpoint, including the login call, invalid bodies, a missing token, and duplicate/missing id cases so you can see the 400/401/404/409 responses too. Open it in IntelliJ IDEA or any editor with the HTTP Client plugin, pick the dev environment from http-client.env.json, and click the green run arrow next to a request starting with login. Later requests reuse the token and the id captured from earlier ones, so running the file top to bottom just works.

## Running locally

With Docker:

```
docker compose up --build
```

This starts the app plus Tempo, Prometheus and Grafana on the same network.

Without Docker, once Java 26 is on your PATH:

```
./gradlew bootRun
```

The app listens on port 8080. Health check is at /actuator/health.

## Testing

The project has several layers of tests.

Unit tests for the service layer, written with JUnit 5 and Mockito.

Web layer tests using WebMvcTest and MockMvc.

Architecture tests with ArchUnit that enforce constructor injection, layering and package boundaries.

Integration tests that spin up a real Postgres container through Testcontainers instead of relying on H2.

API tests written in Karate, running against a fully started app on a random port.

Performance tests written in Gatling under src/gatling. Run them with:

```
docker compose up -d --build app
./gradlew gatlingRun -DbaseUrl=http://localhost:8080
```

Run everything except Gatling with:

```
./gradlew test
```

## CI and security

A GitHub Actions pipeline runs on every push and pull request. It builds and tests the project, lints the Dockerfile with hadolint, scans for leaked secrets with gitleaks, runs CodeQL static analysis, scans dependencies and the built image with Trivy, generates an SBOM and runs an OWASP ZAP baseline scan against the running container. Findings above CRITICAL severity fail the build, lower severities are reported without blocking.

Gatling performance tests run separately, either manually or on a weekly schedule.

Renovate keeps dependencies up to date and opens pull requests for available upgrades.

## Observability

Once docker compose is up, traces can be viewed through Grafana at http://localhost:3000, which is preconfigured with Prometheus and Tempo data sources. Raw metrics are exposed at /actuator/prometheus.
