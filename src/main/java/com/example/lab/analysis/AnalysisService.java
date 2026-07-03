package com.example.lab.analysis;

import com.example.lab.common.NotFoundException;
import com.example.lab.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalysisService {

	private final AnalysisRepository analysisRepository;
	private final UserService userService;

	public AnalysisService(AnalysisRepository analysisRepository, UserService userService) {
		this.analysisRepository = analysisRepository;
		this.userService = userService;
	}

	@Transactional
	public AnalysisResponse create(Long userId, AnalysisRequest request) {
		var user = userService.getOrThrow(userId);
		var analysis = new Analysis(
				user,
				request.testName(),
				request.resultValue(),
				request.unit(),
				request.referenceRange(),
				request.takenAt()
		);
		return AnalysisResponse.from(analysisRepository.save(analysis));
	}

	public List<AnalysisResponse> findAllByUser(Long userId) {
		userService.getOrThrow(userId);
		return analysisRepository.findAllByUserId(userId).stream()
				.map(AnalysisResponse::from)
				.toList();
	}

	public AnalysisResponse findById(Long id) {
		return AnalysisResponse.from(getOrThrow(id));
	}

	public void delete(Long id) {
		var analysis = getOrThrow(id);
		analysisRepository.delete(analysis);
	}

	private Analysis getOrThrow(Long id) {
		return analysisRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("Analysis %d not found".formatted(id)));
	}
}
