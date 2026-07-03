package com.example.lab.analysis;

import jakarta.validation.Valid;
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
public class AnalysisController {

	private final AnalysisService analysisService;

	public AnalysisController(AnalysisService analysisService) {
		this.analysisService = analysisService;
	}

	@PostMapping("/api/users/{userId}/analyses")
	public ResponseEntity<AnalysisResponse> create(@PathVariable Long userId,
			@Valid @RequestBody AnalysisRequest request, UriComponentsBuilder uriBuilder) {
		var created = analysisService.create(userId, request);
		var location = uriBuilder.path("/api/analyses/{id}").buildAndExpand(created.id()).toUri();
		return ResponseEntity.created(location).body(created);
	}

	@GetMapping("/api/users/{userId}/analyses")
	public List<AnalysisResponse> findAllByUser(@PathVariable Long userId) {
		return analysisService.findAllByUser(userId);
	}

	@GetMapping("/api/analyses/{id}")
	public AnalysisResponse findById(@PathVariable Long id) {
		return analysisService.findById(id);
	}

	@DeleteMapping("/api/analyses/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		analysisService.delete(id);
		return ResponseEntity.noContent().build();
	}
}
