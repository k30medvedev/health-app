package com.example.lab.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Managing patients that lab analyses belong to")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping
	@Operation(summary = "Create a user")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "User created"),
			@ApiResponse(responseCode = "400", description = "Request body failed validation",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "409", description = "A user with this email already exists",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error")
	})
	public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request,
			UriComponentsBuilder uriBuilder) {
		var created = userService.create(request);
		var location = uriBuilder.path("/api/users/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@GetMapping
	@Operation(summary = "List users")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Page of users"),
			@ApiResponse(responseCode = "500", description = "Unexpected server error")
	})
	public Page<UserResponse> findAll(Pageable pageable) {
		return userService.findAll(pageable);
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get a user by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "User found"),
			@ApiResponse(responseCode = "404", description = "No user with this id",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error")
	})
	public UserResponse findById(@PathVariable Long id) {
		return userService.findById(id);
	}
}
