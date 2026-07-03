package com.example.lab.user;

import com.example.lab.common.ConflictException;
import com.example.lab.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public UserResponse create(UserRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new ConflictException("User with email '%s' already exists".formatted(request.email()));
		}
		var user = new User(request.fullName(), request.email());
		return UserResponse.from(userRepository.save(user));
	}

	public List<UserResponse> findAll() {
		return userRepository.findAll().stream()
				.map(UserResponse::from)
				.toList();
	}

	public UserResponse findById(Long id) {
		return UserResponse.from(getOrThrow(id));
	}

	public User getOrThrow(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User %d not found".formatted(id)));
	}
}
