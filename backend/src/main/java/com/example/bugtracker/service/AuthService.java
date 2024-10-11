package com.example.bugtracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.bugtracker.repository.UserRepository;
import com.example.bugtracker.security.JwtUtil;
import com.example.bugtracker.dto.UserRegistrationDto;
import com.example.bugtracker.model.User;



@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public String registerUser(UserRegistrationDto registrationDto) {
        System.out.printf("'so far so good2'");

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if(userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        System.out.printf("'so far so good2'");

        try {
            User newUser = new User();
            newUser.setUsername(registrationDto.getUsername());
            newUser.setEmail(registrationDto.getEmail());
            newUser.setPassword(bCryptPasswordEncoder.encode(registrationDto.getPassword()));

            userRepository.save(newUser);
            return jwtUtil.generateToken(newUser);
            
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while registering the user: " + e.getMessage(), e);
        }
    }
    
}

