package com.github.robustapi.service;

import com.github.robustapi.model.SIMCardUser;
import com.github.robustapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SIMCardUserService {

	private final UserRepository userRepository;

	public SIMCardUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public Optional<SIMCardUser> getUserByPhoneNumber(String phoneNumber) {
		return userRepository.findUserByPhoneNumber(phoneNumber);
	}
}
