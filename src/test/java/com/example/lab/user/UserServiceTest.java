package com.example.lab.user;

import com.example.lab.common.ConflictException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class UserServiceTest {

	@Autowired
	private UserService userService;

	@Test
	void createsAndFindsUser() {
		var created = userService.create(new UserRequest("Test User", "test.user@example.com"));

		var found = userService.findById(created.id());

		assertThat(found.email()).isEqualTo("test.user@example.com");
	}

	@Test
	void rejectsDuplicateEmail() {
		userService.create(new UserRequest("First", "dup@example.com"));

		assertThatThrownBy(() -> userService.create(new UserRequest("Second", "dup@example.com")))
				.isInstanceOf(ConflictException.class);
	}
}
