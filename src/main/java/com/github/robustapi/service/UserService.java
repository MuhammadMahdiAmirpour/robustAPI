package com.github.robustapi.service;

import com.github.robustapi.model.User;
import com.github.robustapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Optional<User> getUserByPhoneNumber(String phoneNumber) {
		return userRepository.findUserByPhoneNumber(phoneNumber);
	}
}
