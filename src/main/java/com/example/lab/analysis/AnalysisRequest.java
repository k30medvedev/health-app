package com.example.lab.analysis;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record AnalysisRequest(
		@NotBlank(message = "must not be blank") String testName,
		@NotBlank(message = "must not be blank") String resultValue,
		String unit,
		String referenceRange,
		@NotNull(message = "must not be null") @PastOrPresent(message = "must not be in the future") LocalDate takenAt
) {
}
