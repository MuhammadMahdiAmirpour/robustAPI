package com.github.robustapi.repository;

import com.github.robustapi.model.SIMCardUser;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<SIMCardUser, Long> {
	// @Query(value = "CALL GetUsersByPhoneNumber(:phoneNumber)", nativeQuery = true)
	Optional<SIMCardUser> findUserByPhoneNumber(String phoneNumber);
}
