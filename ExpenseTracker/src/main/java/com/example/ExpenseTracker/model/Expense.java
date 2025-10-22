package com.example.ExpenseTracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal; // For precise monetary calculations
import java.time.LocalDate; // For handling the date of the expense

@Entity
@Table(name = "expenses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal amount; // Use BigDecimal for currency/money

    @Column(nullable = false)
    private LocalDate date;

    // 🌟 RELATIONSHIP 1: MANY-TO-ONE with Category 🌟
    // Many expenses can belong to one category.
    @ManyToOne(fetch = FetchType.LAZY) // Fetch lazily to save resources
    @JoinColumn(name = "category_id", nullable = false) // The foreign key column name
    private Category category;

    // 🌟 RELATIONSHIP 2: MANY-TO-ONE with User 🌟
    // Many expenses belong to one user. This is crucial for security.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}