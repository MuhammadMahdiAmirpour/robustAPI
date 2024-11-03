package com.github.robustapi.service;

import com.github.robustapi.model.SIMCardUser;
import com.github.robustapi.repository.SIMCardUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SIMCardUserService {

	private final SIMCardUserRepository SIMCardUserRepository;

	public SIMCardUserService(SIMCardUserRepository SIMCardUserRepository) {
		this.SIMCardUserRepository = SIMCardUserRepository;
	}

	public Optional<SIMCardUser> getUserByPhoneNumber(String phoneNumber) {
		return SIMCardUserRepository.findUserByPhoneNumber(phoneNumber);
	}
}
