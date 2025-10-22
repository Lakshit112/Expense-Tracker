package com.example.ExpenseTracker.repository;

import com.example.ExpenseTracker.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

// This provides all CRUD database methods (save, findAll, findById, etc.)
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Custom query methods can be added here if needed (e.g., findByName)

}