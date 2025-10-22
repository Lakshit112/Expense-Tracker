package com.example.ExpenseTracker.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Objects; // Optional, but good practice

@Entity
@Table(name = "categories")
@Data // 🌟 THIS GENERATES THE getNAME() METHOD 🌟
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // The @Data annotation automatically creates:
    // public String getName() { return this.name; }
    // public void setName(String name) { this.name = name; }
}