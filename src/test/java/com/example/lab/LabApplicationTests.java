package com.example.lab;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@ResourceLock("postgres-container")
class LabApplicationTests extends AbstractIntegrationTest {

	@Test
	void contextLoads() {
	}
}
