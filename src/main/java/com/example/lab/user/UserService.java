package com.example.lab.user;

import com.example.lab.common.ConflictException;
import com.example.lab.common.NotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		try {
			return UserResponse.from(userRepository.save(user));
		} catch (DataIntegrityViolationException ex) {
			// A concurrent request may have inserted the same email between the existsByEmail
			// check above and this save; the unique constraint catches what the check missed.
			throw new ConflictException("User with email '%s' already exists".formatted(request.email()));
		}
	}

	public Page<UserResponse> findAll(Pageable pageable) {
		return userRepository.findAll(pageable).map(UserResponse::from);
	}

	public UserResponse findById(Long id) {
		return UserResponse.from(getOrThrow(id));
	}

	public User getOrThrow(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("User %d not found".formatted(id)));
	}
}
