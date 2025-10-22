package com.example.ExpenseTracker.controller;

import com.example.ExpenseTracker.model.User;
import com.example.ExpenseTracker.service.UserService;
// 🌟 NEW IMPORTS FOR JWT AND AUTH MANAGER 🌟
import com.example.ExpenseTracker.security.JwtTokenProvider;
import com.example.ExpenseTracker.payload.LoginDto; // Assuming you put LoginDto in 'payload' package
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException; 

@RestController
@RequestMapping("/api/auth") 
public class AuthController {

    // 🌟 INJECTED JWT COMPONENTS 🌟
    private final AuthenticationManager authenticationManager; // Spring Security's mechanism
    private final JwtTokenProvider jwtTokenProvider;         // Our custom token utility
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(
            UserService userService, 
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, // 🌟 NEW INJECTION 🌟
            JwtTokenProvider jwtTokenProvider) {         // 🌟 NEW INJECTION 🌟
        
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager; // 🌟 SET 🌟
        this.jwtTokenProvider = jwtTokenProvider;         // 🌟 SET 🌟
    }

    // 1. REGISTRATION ENDPOINT (Unchanged logic)
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {

        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return new ResponseEntity<>("User with this email already exists!", HttpStatus.BAD_REQUEST);
        }

        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userService.saveUser(user);

        return new ResponseEntity<>("User registered successfully!", HttpStatus.CREATED);
    }

    // 2. 🌟 UPDATED: LOGIN ENDPOINT TO GENERATE AND RETURN TOKEN 🌟
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        
        try {
            // 1. Use AuthenticationManager to handle the authentication process
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getEmail(),
                            loginDto.getPassword()
                    )
            );
            
            // 2. Set authentication in the security context (optional, but good practice)
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 3. Generate the JWT token
            String token = jwtTokenProvider.generateToken(authentication);

            // 4. Return the token directly in the response body
            return ResponseEntity.ok(token); 
            
        } catch (Exception e) {
            // Catch exceptions like BadCredentialsException or DisabledException
            // and return UNAUTHORIZED status.
            return new ResponseEntity<>("Invalid email or password.", HttpStatus.UNAUTHORIZED);
        }
    }
}