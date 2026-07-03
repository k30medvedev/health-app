package com.example.lab.analysis;

import com.example.lab.common.NotFoundException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalysisController.class)
class AnalysisControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private AnalysisService analysisService;

	@Test
	void create_returns201WithLocation_whenRequestIsValid() throws Exception {
		var request = new AnalysisRequest("Hemoglobin", "145", "g/L", "130-160", LocalDate.now());
		var response = new AnalysisResponse(1L, 1L, "Hemoglobin", "145", "g/L", "130-160",
				LocalDate.now(), Instant.now());
		given(analysisService.create(eq(1L), any())).willReturn(response);

		mockMvc.perform(post("/api/users/1/analyses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/analyses/1")))
				.andExpect(jsonPath("$.testName").value("Hemoglobin"));
	}

	@Test
	void create_returns404_whenUserIsMissing() throws Exception {
		var request = new AnalysisRequest("Hemoglobin", "145", "g/L", "130-160", LocalDate.now());
		given(analysisService.create(eq(99L), any())).willThrow(new NotFoundException("User 99 not found"));

		mockMvc.perform(post("/api/users/99/analyses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isNotFound());
	}

	@Test
	void create_returns400_whenBodyIsInvalid() throws Exception {
		var invalidRequest = new AnalysisRequest("", "", null, null, LocalDate.now().plusDays(1));

		mockMvc.perform(post("/api/users/1/analyses")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void findAllByUser_returnsAnalyses() throws Exception {
		given(analysisService.findAllByUser(1L)).willReturn(List.of(
				new AnalysisResponse(1L, 1L, "Glucose", "5.1", "mmol/L", "3.9-5.6", LocalDate.now(), Instant.now())
		));

		mockMvc.perform(get("/api/users/1/analyses"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].testName").value("Glucose"));
	}

	@Test
	void findById_returns200_whenExists() throws Exception {
		given(analysisService.findById(1L)).willReturn(
				new AnalysisResponse(1L, 1L, "Glucose", "5.1", "mmol/L", "3.9-5.6", LocalDate.now(), Instant.now()));

		mockMvc.perform(get("/api/analyses/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void findById_returns404_whenMissing() throws Exception {
		given(analysisService.findById(99L)).willThrow(new NotFoundException("Analysis 99 not found"));

		mockMvc.perform(get("/api/analyses/99"))
				.andExpect(status().isNotFound());
	}

	@Test
	void delete_returns204_whenExists() throws Exception {
		mockMvc.perform(delete("/api/analyses/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	void delete_returns404_whenMissing() throws Exception {
		org.mockito.Mockito.doThrow(new NotFoundException("Analysis 99 not found"))
				.when(analysisService).delete(99L);

		mockMvc.perform(delete("/api/analyses/99"))
				.andExpect(status().isNotFound());
	}
}
