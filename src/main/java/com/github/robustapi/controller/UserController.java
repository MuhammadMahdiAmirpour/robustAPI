package com.github.robustapi.controller;

import com.github.robustapi.model.User;
import com.github.robustapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/by-phone/{phoneNumber}")
	public ResponseEntity<User> getUsersByPhoneNumber(@PathVariable String phoneNumber) {
		return userService.getUserByPhoneNumber(phoneNumber).map(ResponseEntity::ok).orElse(
				ResponseEntity.notFound().build());
	}
}
