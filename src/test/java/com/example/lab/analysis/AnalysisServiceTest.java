package com.example.lab.analysis;

import com.example.lab.common.NotFoundException;
import com.example.lab.user.User;
import com.example.lab.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AnalysisServiceTest {

	@Mock
	private AnalysisRepository analysisRepository;

	@Mock
	private UserService userService;

	private AnalysisService analysisService;

	@BeforeEach
	void setUp() {
		analysisService = new AnalysisService(analysisRepository, userService);
	}

	@Test
	void create_savesAnalysis_whenUserExists() {
		var user = new User("Test User", "test.user@example.com");
		var request = new AnalysisRequest("Hemoglobin", "145", "g/L", "130-160", LocalDate.now());
		given(userService.getOrThrow(1L)).willReturn(user);
		given(analysisRepository.save(any(Analysis.class))).willAnswer(invocation -> invocation.getArgument(0));

		var response = analysisService.create(1L, request);

		assertThat(response.testName()).isEqualTo("Hemoglobin");
		assertThat(response.resultValue()).isEqualTo("145");
	}

	@Test
	void create_throwsNotFound_whenUserIsMissing() {
		var request = new AnalysisRequest("Hemoglobin", "145", "g/L", "130-160", LocalDate.now());
		given(userService.getOrThrow(99L)).willThrow(new NotFoundException("User 99 not found"));

		assertThatThrownBy(() -> analysisService.create(99L, request))
				.isInstanceOf(NotFoundException.class);

		verify(analysisRepository, never()).save(any());
	}

	@Test
	void findAllByUser_returnsAnalyses_whenUserExists() {
		var user = new User("Test User", "test.user@example.com");
		given(userService.getOrThrow(1L)).willReturn(user);
		given(analysisRepository.findAllByUserId(1L)).willReturn(List.of(
				new Analysis(user, "Glucose", "5.1", "mmol/L", "3.9-5.6", LocalDate.now())
		));

		var result = analysisService.findAllByUser(1L);

		assertThat(result).hasSize(1);
		assertThat(result.get(0).testName()).isEqualTo("Glucose");
	}

	@Test
	void findAllByUser_throwsNotFound_whenUserIsMissing() {
		given(userService.getOrThrow(99L)).willThrow(new NotFoundException("User 99 not found"));

		assertThatThrownBy(() -> analysisService.findAllByUser(99L))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void findById_returnsAnalysis_whenExists() {
		var user = new User("Test User", "test.user@example.com");
		var analysis = new Analysis(user, "Cholesterol", "4.8", "mmol/L", "<5.2", LocalDate.now());
		given(analysisRepository.findById(1L)).willReturn(Optional.of(analysis));

		var response = analysisService.findById(1L);

		assertThat(response.testName()).isEqualTo("Cholesterol");
	}

	@Test
	void findById_throwsNotFound_whenMissing() {
		given(analysisRepository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> analysisService.findById(99L))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void delete_removesAnalysis_whenExists() {
		var user = new User("Test User", "test.user@example.com");
		var analysis = new Analysis(user, "Cholesterol", "4.8", "mmol/L", "<5.2", LocalDate.now());
		given(analysisRepository.findById(1L)).willReturn(Optional.of(analysis));

		analysisService.delete(1L);

		verify(analysisRepository).delete(analysis);
	}

	@Test
	void delete_throwsNotFound_whenMissing() {
		given(analysisRepository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> analysisService.delete(99L))
				.isInstanceOf(NotFoundException.class);

		verify(analysisRepository, never()).delete(any());
	}
}
