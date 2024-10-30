package com.github.robustapi.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class RequestDetails {
	@NotBlank
	private String userAgent;
	@NotBlank
	private String sourceIp;
	@NotBlank
	private UUID requestId;
}
