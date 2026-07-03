package com.example.lab.client;

import com.example.lab.user.UserResponse;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Mirrors just the part of Spring Data's Page JSON payload the Feign demo needs;
 * the rest (pageable, sort, etc.) is ignored rather than modeled.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record UserPage(List<UserResponse> content) {
}
