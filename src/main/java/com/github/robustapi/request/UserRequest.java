package com.github.robustapi.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UserRequest implements Serializable {
	@NotBlank
	private UUID analyzerId;
	@NotBlank
	private String phoneNumber;
	@NotBlank
	private RequestDetails requestDetails;

	public void setRequestId(UUID requestId) {
		this.requestDetails.setRequestId(requestId);
	}
}
