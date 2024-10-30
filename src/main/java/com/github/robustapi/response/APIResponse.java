package com.github.robustapi.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class APIResponse implements Serializable {
	private String nationalId;
	private UUID requestId;
	private String firstName;
	private String lastName;
	private String birthDate;
	private String address;
	private String message;
}
