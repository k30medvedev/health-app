package com.example.lab.user;

import com.example.lab.common.ConflictException;
import com.example.lab.common.NotFoundException;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private UserService userService;

	@Test
	void create_returns201WithLocation_whenRequestIsValid() throws Exception {
		var request = new UserRequest("Test User", "test.user@example.com");
		var response = new UserResponse(1L, "Test User", "test.user@example.com", Instant.now());
		given(userService.create(any())).willReturn(response);

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/users/1")))
				.andExpect(jsonPath("$.email").value("test.user@example.com"));
	}

	@Test
	void create_returns400_whenBodyIsInvalid() throws Exception {
		var invalidRequest = new UserRequest("", "not-an-email");

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(invalidRequest)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void create_returns409_whenEmailAlreadyExists() throws Exception {
		var request = new UserRequest("Test User", "dup@example.com");
		given(userService.create(any())).willThrow(new ConflictException("User with email 'dup@example.com' already exists"));

		mockMvc.perform(post("/api/users")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isConflict());
	}

	@Test
	void findAll_returnsUsers() throws Exception {
		given(userService.findAll(any())).willReturn(new PageImpl<>(List.of(
				new UserResponse(1L, "Test User", "test.user@example.com", Instant.now())
		)));

		mockMvc.perform(get("/api/users"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content[0].email").value("test.user@example.com"));
	}

	@Test
	void findById_returns200_whenUserExists() throws Exception {
		given(userService.findById(eq(1L))).willReturn(
				new UserResponse(1L, "Test User", "test.user@example.com", Instant.now()));

		mockMvc.perform(get("/api/users/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1));
	}

	@Test
	void findById_returns404_whenUserIsMissing() throws Exception {
		given(userService.findById(eq(99L))).willThrow(new NotFoundException("User 99 not found"));

		mockMvc.perform(get("/api/users/99"))
				.andExpect(status().isNotFound());
	}
}
