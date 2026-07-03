package com.example.lab.analysis;

import java.time.Instant;
import java.time.LocalDate;

public record AnalysisResponse(
		Long id,
		Long userId,
		String testName,
		String resultValue,
		String unit,
		String referenceRange,
		LocalDate takenAt,
		Instant createdAt
) {

	static AnalysisResponse from(Analysis analysis) {
		return new AnalysisResponse(
				analysis.getId(),
				analysis.getUser().getId(),
				analysis.getTestName(),
				analysis.getResultValue(),
				analysis.getUnit(),
				analysis.getReferenceRange(),
				analysis.getTakenAt(),
				analysis.getCreatedAt()
		);
	}
}
