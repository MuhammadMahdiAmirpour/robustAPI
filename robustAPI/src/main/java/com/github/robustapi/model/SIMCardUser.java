package com.github.robustapi.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Builder
public class SIMCardUser {
	@Id
	@Column(unique = true)
	@JsonProperty("nationalId")
	private String nationalId;
	@JsonProperty("firstName") private String firstName;
	@JsonProperty("lastName") private String lastName;
	@JsonProperty("birthDate") private String birthDate;
	@JsonProperty("address") private String address;
	@Column(unique = true)
	@JsonProperty("phoneNumber")
	private String phoneNumber;
}
