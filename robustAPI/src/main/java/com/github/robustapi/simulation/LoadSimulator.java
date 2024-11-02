package com.github.robustapi.simulation;

import com.github.robustapi.request.APIRequest;
import com.github.robustapi.request.APIRequestDetails;
import com.github.robustapi.response.APIResponse;
import com.github.robustapi.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class LoadSimulator {

    @Value("${api.endpoint.url}") // Define your API endpoint in application.properties
    private String apiEndpoint;

    private final MonitoringService monitoringService;
    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;

    private volatile boolean running = true;

    @Autowired
    public LoadSimulator(MonitoringService monitoringService, RestTemplate restTemplate, JdbcTemplate jdbcTemplate) {
        this.monitoringService = monitoringService;
        this.restTemplate = restTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void startSimulation(int numberOfAnalyzers) {
        ExecutorService executor = Executors.newFixedThreadPool(numberOfAnalyzers);

        for (int i = 0; i < numberOfAnalyzers; i++) {
            final String analyzerId = "analyzer_" + i; // Unique identifier for each analyzer
            executor.submit(() -> {
                while (running) {
                    String phoneNumber = fetchRandomPhoneNumber(); // Fetch a random phone number from the database
                    if (phoneNumber != null) {
                        sendApiRequest(analyzerId, phoneNumber);
                    }
                    try {
                        Thread.sleep(100); // Adjust frequency of requests here (e.g., every 100 ms)
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }

        executor.shutdown(); // Shutdown the executor after tasks are submitted
    }

    private String fetchRandomPhoneNumber() {
        // Fetch a random phone number from the database
        List<String> phoneNumbers = jdbcTemplate.queryForList("SELECT phone_number FROM api_logs ORDER BY RANDOM() LIMIT 1", String.class);
        return phoneNumbers.isEmpty() ? null : phoneNumbers.get(0);
    }

    private void sendApiRequest(String analyzerId, String phoneNumber) {
        APIRequest apiRequest = null;
        try {
            // Create the API request object with the specified structure
            apiRequest = APIRequest.builder()
                    .analyzerId(UUID.fromString(analyzerId)) // Use UUID for analyzerId
                    .phoneNumber(phoneNumber)
                    .requestDetails(APIRequestDetails.builder()
                            .userAgent("Mozilla/5.0") // Example user agent
                            .sourceIp("192.168.1.1") // Example source IP
                            .requestId(UUID.randomUUID()) // Generate a unique request ID
                            .build())
                    .build();

            // Send the request using RestTemplate
            APIResponse response = restTemplate.postForObject(apiEndpoint + "/by-phone/", apiRequest, APIResponse.class);

            if (response != null) {
                System.out.printf("Successful request from %s: Response: %s%n", analyzerId, response.getMessage());
                monitoringService.logApiRequest(analyzerId, phoneNumber, 200L, "SUCCESS", apiRequest.toString().length());
            } else {
                System.err.printf("Failed request from %s: No response received%n", analyzerId);
                monitoringService.logApiRequest(analyzerId, phoneNumber, 500L, "ERROR", apiRequest.toString().length());
            }
        } catch (Exception e) {
            System.err.printf("Error sending request from %s: %s%n", analyzerId, e.getMessage());
            monitoringService.logApiRequest(analyzerId, phoneNumber, 500L, "EXCEPTION: " + e.getMessage(), apiRequest.toString().length());
        }
    }

    public void stopSimulation() {
        running = false; // Stop the load simulation
    }
}
