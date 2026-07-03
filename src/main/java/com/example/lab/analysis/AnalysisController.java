package com.example.lab.analysis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@Tag(name = "Analyses", description = "Lab analyses belonging to a user")
public class AnalysisController {

	private final AnalysisService analysisService;

	public AnalysisController(AnalysisService analysisService) {
		this.analysisService = analysisService;
	}

	@PostMapping("/api/users/{userId}/analyses")
	@Operation(summary = "Add an analysis for a user")
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Analysis created"),
			@ApiResponse(responseCode = "400", description = "Request body failed validation",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "404", description = "No user with this id",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error")
	})
	public ResponseEntity<AnalysisResponse> create(@PathVariable Long userId,
			@Valid @RequestBody AnalysisRequest request, UriComponentsBuilder uriBuilder) {
		var created = analysisService.create(userId, request);
		var location = uriBuilder.path("/api/analyses/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@GetMapping("/api/users/{userId}/analyses")
	@Operation(summary = "List a user's analyses")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Analyses found"),
			@ApiResponse(responseCode = "404", description = "No user with this id",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error")
	})
	public List<AnalysisResponse> findAllByUser(@PathVariable Long userId) {
		return analysisService.findAllByUser(userId);
	}

	@GetMapping("/api/analyses/{id}")
	@Operation(summary = "Get an analysis by id")
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Analysis found"),
			@ApiResponse(responseCode = "404", description = "No analysis with this id",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error")
	})
	public AnalysisResponse findById(@PathVariable Long id) {
		return analysisService.findById(id);
	}

	@DeleteMapping("/api/analyses/{id}")
	@Operation(summary = "Delete an analysis")
	@ApiResponses({
			@ApiResponse(responseCode = "204", description = "Analysis deleted"),
			@ApiResponse(responseCode = "404", description = "No analysis with this id",
					content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
			@ApiResponse(responseCode = "500", description = "Unexpected server error")
	})
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		analysisService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
