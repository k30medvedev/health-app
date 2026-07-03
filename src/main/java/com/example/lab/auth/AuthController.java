package com.example.lab.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Stub login for demo purposes: one hardcoded credential and no persisted user
 * store. It exists to hand out a real, verifiable JWT so the rest of the API
 * can require a bearer token, not to model actual user management.
 */
@RestController
@Tag(name = "Auth", description = "Demo login that issues a bearer token for the rest of the API")
public class AuthController {

	private static final String DEMO_USERNAME = "demo";
	private static final String DEMO_PASSWORD = "demo123";

	private final JwtIssuer jwtIssuer;

	public AuthController(JwtIssuer jwtIssuer) {
		this.jwtIssuer = jwtIssuer;
	}

	@PostMapping("/api/auth/login")
	@Operation(summary = "Log in with the demo credentials (demo / demo123) and get a bearer token")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Login succeeded, token returned"),
			@ApiResponse(responseCode = "401", description = "Wrong username or password")
	})
	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
		if (!DEMO_USERNAME.equals(request.username()) || !DEMO_PASSWORD.equals(request.password())) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		return ResponseEntity.ok(new AuthResponse(jwtIssuer.issue(request.username())));
	}
}
