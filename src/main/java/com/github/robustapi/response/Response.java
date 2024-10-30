package com.github.robustapi.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Response {
	private String nationalId;
	private UUID requestId;
	private String firstName;
	private String lastName;
	private String birthDate;
	private       String address;
	private final String Message = "Success";
}
