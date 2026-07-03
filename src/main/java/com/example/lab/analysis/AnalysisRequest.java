package com.example.lab.analysis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record AnalysisRequest(
		@NotBlank(message = "must not be blank") @Size(max = 255, message = "must be at most 255 characters") String testName,
		@NotBlank(message = "must not be blank") @Size(max = 100, message = "must be at most 100 characters") String resultValue,
		@Size(max = 50, message = "must be at most 50 characters") String unit,
		@Size(max = 100, message = "must be at most 100 characters") String referenceRange,
		@NotNull(message = "must not be null") @PastOrPresent(message = "must not be in the future") LocalDate takenAt
) {
}
