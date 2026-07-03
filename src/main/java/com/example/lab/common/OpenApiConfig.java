package com.example.lab.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI healthAppOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("Health App API")
						.description("Lab playground for trying out Spring/Gradle/Docker/CI security tooling: "
								+ "manages users and the lab analyses attached to them.")
						.version("v1"));
	}
}
