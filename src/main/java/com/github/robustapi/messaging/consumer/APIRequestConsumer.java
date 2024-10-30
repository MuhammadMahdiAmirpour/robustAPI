package com.github.robustapi.messaging.consumer;

import com.github.robustapi.config.RabbitConfig;
import com.github.robustapi.model.User;
import com.github.robustapi.request.APIRequest;
import com.github.robustapi.response.APIResponse;
import com.github.robustapi.service.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class APIRequestConsumer {

	private final UserService userService;

	public APIRequestConsumer(UserService userService) {
		this.userService = userService;
	}

	@RabbitListener(queues = RabbitConfig.QUEUE_NAME)
	@SendTo // Directly sends the response back for the RPC call
	public APIResponse handleUserRequest(APIRequest apiRequest) {
		Optional<User> userOpt = userService.getUserByPhoneNumber(apiRequest.getPhoneNumber());

		if (userOpt.isPresent()) {
			User user = userOpt.get();
			return APIResponse.builder()
					       .nationalId(user.getNationalId())
					       .requestId(UUID.fromString(String.valueOf(apiRequest.getRequestDetails().getRequestId())))
					       .firstName(user.getFirstName())
					       .lastName(user.getLastName())
					       .birthDate(user.getBirthDate())
					       .address(user.getAddress())
					       .message("Success")
					       .build();
		} else {
			return APIResponse.builder()
					       .requestId(UUID.fromString(String.valueOf(apiRequest.getRequestDetails().getRequestId())))
					       .message("User not found")
					       .build();
		}
	}
}
