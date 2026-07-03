package com.example.lab.client;

import com.example.lab.user.UserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "health-app-users", url = "${clients.health-app.base-url}")
public interface UserApiClient {

	@GetMapping("/api/users")
	UserPage findAll();

	@GetMapping("/api/users/{id}")
	UserResponse findById(@PathVariable("id") Long id);
}
