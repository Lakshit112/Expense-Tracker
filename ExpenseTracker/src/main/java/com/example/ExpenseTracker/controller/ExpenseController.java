package com.example.ExpenseTracker.controller;

import com.example.ExpenseTracker.model.Expense;
import com.example.ExpenseTracker.model.User;
import com.example.ExpenseTracker.repository.ExpenseRepository;
import com.example.ExpenseTracker.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository; // Needed for fetching the full User object

    // Constructor Injection
    public ExpenseController(ExpenseRepository expenseRepository, UserRepository userRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
    }

    // --- GET ALL EXPENSES FOR THE AUTHENTICATED USER ---
    @GetMapping
    public List<Expense> getAllUserExpenses(@AuthenticationPrincipal User user) {
        // Find expenses by the current authenticated user's ID
        return expenseRepository.findByUserId(user.getId());
    }

    // --- CREATE NEW EXPENSE ---
    @PostMapping
    public ResponseEntity<Expense> createExpense(
            @AuthenticationPrincipal User user,
            @RequestBody Expense expense) {

        // 1. Assign the expense to the currently authenticated user
        expense.setUser(user);

        // 2. Ensure Category is set (often sent in the JSON body)
        // Note: You should ideally validate the category exists here!

        Expense savedExpense = expenseRepository.save(expense);
        return new ResponseEntity<>(savedExpense, HttpStatus.CREATED);
    }

    // --- GET EXPENSE BY ID ---
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getExpenseById(@AuthenticationPrincipal User currentUser, @PathVariable Long id) {
        Optional<Expense> expenseOptional = expenseRepository.findById(id);

        if (expenseOptional.isPresent() && expenseOptional.get().getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.ok(expenseOptional.get());
        } else {
            // Return 404 if not found OR if found but doesn't belong to the user
            return ResponseEntity.notFound().build();
        }
    }

    // --- UPDATE EXISTING EXPENSE ---
    @PutMapping("/{id}")
    public ResponseEntity<Expense> updateExpense(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @RequestBody Expense expenseDetails) {

        Optional<Expense> expenseOptional = expenseRepository.findById(id);

        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();

            // 1. Security Check: Ensure the expense belongs to the current user
            if (!expense.getUser().getId().equals(currentUser.getId())) {
                // Return 403 Forbidden if the user doesn't own this expense
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // 2. Update the fields
            expense.setDescription(expenseDetails.getDescription());
            expense.setAmount(expenseDetails.getAmount());
            expense.setDate(expenseDetails.getDate());
            // It's crucial to set the category here from the request body as well
            expense.setCategory(expenseDetails.getCategory());

            final Expense updatedExpense = expenseRepository.save(expense);
            return ResponseEntity.ok(updatedExpense);

        } else {
            // Return 404 Not Found if the expense ID is invalid
            return ResponseEntity.notFound().build();
        }
    }

    // --- DELETE EXPENSE ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExpense(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {

        Optional<Expense> expenseOptional = expenseRepository.findById(id);

        if (expenseOptional.isPresent()) {
            Expense expense = expenseOptional.get();

            // 1. Security Check: Ensure the expense belongs to the current user
            if (!expense.getUser().getId().equals(currentUser.getId())) {
                // Return 403 Forbidden
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            // 2. Delete the expense
            expenseRepository.delete(expense);
            // Return 204 No Content (Standard for successful deletion)
            return ResponseEntity.noContent().build();

        } else {
            // If the expense is not found, we still often return a success status
            // to avoid revealing information, but 404 is also acceptable here.
            return ResponseEntity.notFound().build();
        }
    }
}