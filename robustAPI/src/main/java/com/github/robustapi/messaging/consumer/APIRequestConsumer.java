package com.github.robustapi.messaging.consumer;

import com.github.robustapi.config.RabbitConfig;
import com.github.robustapi.model.SIMCardUser;
import com.github.robustapi.request.APIRequest;
import com.github.robustapi.response.APIResponse;
import com.github.robustapi.service.SIMCardUserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class APIRequestConsumer {

    private final SIMCardUserService SIMCardUserService;

    public APIRequestConsumer(SIMCardUserService SIMCardUserService) {
        this.SIMCardUserService = SIMCardUserService;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    @SendTo // Directly sends the response back for the RPC call
    public APIResponse handleUserRequest(APIRequest apiRequest) {
        Optional<SIMCardUser> userOpt = SIMCardUserService.getUserByPhoneNumber(apiRequest.getPhoneNumber());

        if (userOpt.isPresent()) {
            SIMCardUser SIMCardUser = userOpt.get();
            return APIResponse.builder().nationalId(SIMCardUser.getNationalId()).requestId(
                    UUID.fromString(String.valueOf(apiRequest.getRequestDetails().getRequestId()))).firstName(
                    SIMCardUser.getFirstName()).lastName(SIMCardUser.getLastName()).birthDate(SIMCardUser.getBirthDate()).address(
                    SIMCardUser.getAddress()).message("Success").build();
        } else {
            return APIResponse.builder().requestId(
                    UUID.fromString(String.valueOf(apiRequest.getRequestDetails().getRequestId()))).message(
                    "SIMCardUser not found").build();
        }
    }
}