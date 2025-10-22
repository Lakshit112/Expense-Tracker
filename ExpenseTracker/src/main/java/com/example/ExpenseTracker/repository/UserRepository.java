package com.example.ExpenseTracker.repository;

import com.example.ExpenseTracker.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // Custom query method for Spring Security to find a user by email/username
    Optional<User> findByEmail(String email);
}