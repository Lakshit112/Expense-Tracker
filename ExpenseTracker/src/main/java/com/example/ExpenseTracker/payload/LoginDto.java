package com.example.ExpenseTracker.payload; // Use your desired DTO/payload package

import lombok.Data;

@Data
public class LoginDto {
    private String email;
    private String password;
}