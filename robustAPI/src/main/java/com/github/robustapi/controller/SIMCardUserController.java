package com.github.robustapi.controller;

import com.github.robustapi.config.RabbitConfig;
import com.github.robustapi.request.APIRequest;
import com.github.robustapi.response.APIResponse;
import com.github.robustapi.service.MonitoringService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class SIMCardUserController {

    private final RabbitTemplate rabbitTemplate;
    private final MonitoringService monitoringService;

    @Autowired
    public SIMCardUserController(RabbitTemplate rabbitTemplate, MonitoringService monitoringService) {
        this.rabbitTemplate = rabbitTemplate;
        this.monitoringService = monitoringService;
    }

    @PostMapping("/by-phone/")
    public ResponseEntity<APIResponse> getUsersByPhoneNumber(@RequestBody APIRequest apiRequest) {
        long startTime = System.currentTimeMillis();

        try {
            // Send and receive the response synchronously over RabbitMQ
            APIResponse response = (APIResponse) rabbitTemplate.convertSendAndReceive(RabbitConfig.EXCHANGE_NAME,
                    RabbitConfig.ROUTING_KEY, apiRequest
            );

            // Calculate response time
            long responseTime = System.currentTimeMillis() - startTime;

            if (response == null) {
                // Log failed request
                monitoringService.logApiRequest(String.valueOf(apiRequest.getAnalyzerId()), apiRequest.getPhoneNumber(),
                        responseTime, "ERROR", apiRequest.toString().length()
                );
                return ResponseEntity.status(500).body(
                        APIResponse.builder().message("Failed to process request").build());
            }

            // Log successful request
            monitoringService.logApiRequest(String.valueOf(apiRequest.getAnalyzerId()), apiRequest.getPhoneNumber(),
                    responseTime, "SUCCESS", apiRequest.toString().length()
            );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Log exception
            monitoringService.logApiRequest(String.valueOf(apiRequest.getAnalyzerId()), apiRequest.getPhoneNumber(),
                    System.currentTimeMillis() - startTime, "EXCEPTION: " + e.getMessage(),
                    apiRequest.toString().length()
            );

            return ResponseEntity.status(500).body(
                    APIResponse.builder().message("Error processing request: " + e.getMessage()).build());
        }
    }

    @PostMapping("/log")
    public ResponseEntity<String> logRequest(
            @RequestParam String analyzerId, @RequestParam String phoneNumber, @RequestParam long responseTime,
            @RequestParam String status, @RequestParam int requestSize
    ) {

        // Call the MonitoringService.logApiRequest method
        monitoringService.logApiRequest(analyzerId, phoneNumber, responseTime, status, requestSize);

        return ResponseEntity.ok("Log successfully recorded.");
    }
}