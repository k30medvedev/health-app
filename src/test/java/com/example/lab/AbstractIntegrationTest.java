package com.example.lab;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base class for integration tests that need a real relational database instead of the
 * H2 in-memory instance the application uses at runtime, so Flyway migrations and JPA
 * mappings get exercised against the same engine (Postgres) a real deployment would use.
 * The container is started once per JVM and shared across subclasses.
 */
@Testcontainers
public abstract class AbstractIntegrationTest {

	@Container
	@ServiceConnection
	static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");
}
