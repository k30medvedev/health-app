package com.example.lab.client;

import com.example.lab.user.UserResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Calls this same service's REST API through a Feign client instead of a plain
 * RestClient, as a stand-in for how a downstream consumer would talk to it
 * once it's deployed somewhere on the network.
 */
@RestController
@RequestMapping("/api/demo/feign")
public class FeignDemoController {

	private final UserApiClient userApiClient;

	public FeignDemoController(UserApiClient userApiClient) {
		this.userApiClient = userApiClient;
	}

	@GetMapping("/users")
	public List<UserResponse> findAllUsers() {
		return userApiClient.findAll();
	}

	@GetMapping("/users/{id}")
	public UserResponse findUserById(@PathVariable Long id) {
		return userApiClient.findById(id);
	}
}
