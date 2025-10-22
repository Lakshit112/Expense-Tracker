package com.example.ExpenseTracker.config;

import com.example.ExpenseTracker.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter authenticationFilter;

    // Constructor Injection for the JWT Filter
    public SecurityConfig(JwtAuthenticationFilter authenticationFilter) {
        this.authenticationFilter = authenticationFilter;
    }

    // 1. Password Encoder Bean
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. AuthenticationManager Bean
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 3. Security Filter Chain Bean (FIXED)
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF (Crucial for REST APIs using JWT)
                .csrf(csrf -> csrf.disable())

                // 2. Configure session management to be STATELESS for JWT
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. Define Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // 🌟 FIX 1: Allow EVERYONE access to Auth endpoints (login/register) 🌟
                        .requestMatchers("/api/auth/**").permitAll()

                        // 🌟 FIX 2: Require the 'USER' role for protected API endpoints 🌟
                        // This fixes the 403 on GET/POST /api/expenses and /api/categories
                        .requestMatchers("/api/expenses/**").hasRole("USER")
                        .requestMatchers("/api/categories/**").hasRole("USER")

                        // 4. All other requests require authentication (safety net)
                        .anyRequest().authenticated()
                )
                // Disable default form login and basic http
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable());

        // 5. Register the JWT filter in the security chain
        // This ensures the token is validated BEFORE Spring Security's default filters run.
        http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}