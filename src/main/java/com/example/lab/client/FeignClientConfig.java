package com.example.lab.client;

import com.example.lab.auth.JwtIssuer;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Applies to every Feign client in the app (Spring Cloud OpenFeign picks up
 * any RequestInterceptor bean automatically). Mints a fresh token in-process
 * for each outgoing call, since UserApiClient is now talking to endpoints
 * that require authentication.
 */
@Configuration
public class FeignClientConfig {

	@Bean
	public RequestInterceptor bearerTokenInterceptor(JwtIssuer jwtIssuer) {
		return template -> template.header("Authorization", "Bearer " + jwtIssuer.issue("feign-client"));
	}
}
