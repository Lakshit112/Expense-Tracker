package com.example.ExpenseTracker.repository;

import com.example.ExpenseTracker.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // 🌟 CRITICAL METHOD FOR SECURITY 🌟
    // Custom query method to find all expenses owned by a specific User ID.
    List<Expense> findByUserId(Long userId);
}