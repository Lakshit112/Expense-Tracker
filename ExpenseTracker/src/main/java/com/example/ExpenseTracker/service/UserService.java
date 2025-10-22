package com.example.ExpenseTracker.service;

import com.example.ExpenseTracker.model.User;
import com.example.ExpenseTracker.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.ExpenseTracker.model.User;
import java.util.Optional;

@Service // Marks this class as a Spring Service component
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // Dependency Injection: Spring automatically provides the UserRepository
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method is required by the UserDetailsService interface.
     * It is used by Spring Security to load user details for authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // We use the findByEmail method we defined in the UserRepository
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // You can add other business logic methods here later, like registerUser()
    // or updateUserDetails().

}