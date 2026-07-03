package com.example.lab.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(
		@NotBlank(message = "must not be blank") String fullName,
		@NotBlank(message = "must not be blank") @Email(message = "must be a valid email") String email
) {
}
