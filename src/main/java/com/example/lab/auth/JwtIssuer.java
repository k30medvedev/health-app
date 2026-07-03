package com.example.lab.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class JwtIssuer {

	private final JwtEncoder jwtEncoder;
	private final Duration validity;

	public JwtIssuer(JwtEncoder jwtEncoder, @Value("${security.jwt.validity}") Duration validity) {
		this.jwtEncoder = jwtEncoder;
		this.validity = validity;
	}

	public String issue(String subject) {
		var now = Instant.now();
		var claims = JwtClaimsSet.builder()
				.issuer("health-app")
				.subject(subject)
				.issuedAt(now)
				.expiresAt(now.plus(validity))
				.build();
		var header = JwsHeader.with(MacAlgorithm.HS256).build();
		return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}
}
