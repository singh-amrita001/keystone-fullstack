package com.zidio.keystone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.zidio.keystone.entity.User;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	List<User> findByRoleIgnoreCase(String role);
}