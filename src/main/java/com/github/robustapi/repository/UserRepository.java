package com.github.robustapi.repository;

import com.github.robustapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
	@Query(value = "CALL GetUsersByPhoneNumber(:phoneNumber)", nativeQuery = true)
	Optional<User> findUserByPhoneNumber(String phoneNumber);
}
