package com.github.robustapi.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class APIRequestDetails {
	@NotBlank private String userAgent;
	@NotBlank private String sourceIp;
	@NotBlank private UUID requestId;
}
