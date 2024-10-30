package com.github.robustapi.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class APIRequest {
	@NotBlank
	private UUID analyzerId;
	@NotBlank
	private String phoneNumber;
	@NotBlank
	private APIRequestDetails requestDetails;
}
