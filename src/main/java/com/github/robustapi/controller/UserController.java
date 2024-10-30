package com.github.robustapi.controller;

import com.github.robustapi.config.RabbitConfig;
import com.github.robustapi.request.APIRequest;
import com.github.robustapi.response.APIResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

	private final RabbitTemplate rabbitTemplate;

	public UserController(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@PostMapping("/by-phone/")
	public ResponseEntity<APIResponse> getUsersByPhoneNumber(@RequestBody APIRequest apiRequest) {
		// Send and receive the response synchronously over RabbitMQ
		APIResponse response = (APIResponse) rabbitTemplate.convertSendAndReceive(
				RabbitConfig.EXCHANGE_NAME, RabbitConfig.ROUTING_KEY, apiRequest);

		// Check for any failure in response
		if (response == null) {
			return ResponseEntity.status(500).body(
					APIResponse.builder()
							.message("Failed to process request")
							.build()
			);
		}
		return ResponseEntity.ok(response);
	}
}
