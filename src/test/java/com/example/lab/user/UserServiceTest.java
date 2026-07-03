package com.example.lab.user;

import com.example.lab.common.ConflictException;
import com.example.lab.common.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository);
	}

	@Test
	void create_savesUser_whenEmailIsNotTaken() {
		var request = new UserRequest("Test User", "test.user@example.com");
		given(userRepository.existsByEmail(request.email())).willReturn(false);
		given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

		var response = userService.create(request);

		assertThat(response.fullName()).isEqualTo("Test User");
		assertThat(response.email()).isEqualTo("test.user@example.com");
	}

	@Test
	void create_throwsConflict_whenEmailIsAlreadyTaken() {
		var request = new UserRequest("Test User", "dup@example.com");
		given(userRepository.existsByEmail(request.email())).willReturn(true);

		assertThatThrownBy(() -> userService.create(request))
				.isInstanceOf(ConflictException.class);

		verify(userRepository, never()).save(any());
	}

	@Test
	void findById_returnsUser_whenExists() {
		var user = new User("Test User", "test.user@example.com");
		given(userRepository.findById(1L)).willReturn(Optional.of(user));

		var response = userService.findById(1L);

		assertThat(response.email()).isEqualTo("test.user@example.com");
	}

	@Test
	void findById_throwsNotFound_whenMissing() {
		given(userRepository.findById(99L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userService.findById(99L))
				.isInstanceOf(NotFoundException.class);
	}

	@Test
	void findAll_returnsMappedUsers() {
		given(userRepository.findAll()).willReturn(List.of(
				new User("First", "first@example.com"),
				new User("Second", "second@example.com")
		));

		var result = userService.findAll();

		assertThat(result).hasSize(2)
				.extracting(UserResponse::email)
				.containsExactly("first@example.com", "second@example.com");
	}

	@Test
	void getOrThrow_throwsNotFound_whenMissing() {
		given(userRepository.findById(42L)).willReturn(Optional.empty());

		assertThatThrownBy(() -> userService.getOrThrow(42L))
				.isInstanceOf(NotFoundException.class);
	}
}
