package com.example.bugtracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bugtracker.dto.UserRegistrationDto;
import com.example.bugtracker.dto.UserSignInDto;


import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.hibernate.mapping.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


import com.example.bugtracker.service.AuthService;




@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    //todo add email verification
    //todo add password confirmation
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, String>> userRegistration(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            String token = authService.registerUser(userRegistrationDto);
            Map<String, String> response = new HashMap<>();
            response.put("JWT token", token);
            return ResponseEntity.ok(response); 

        } catch(Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "An unexpected error occurred."));
        }
        }


    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> userSignIn(@Valid @RequestBody UserSignInDto userSignInDto){
        try {
            String token = authService.userSignIn(userSignInDto);
            Map<String, String> response = new HashMap<>();
            response.put("JWT Token", token);
            return ResponseEntity.ok(response);
        } catch(Exception e) {
            return ResponseEntity.status(500).body(Collections.singletonMap("error", "An unexpected error occurred."));
        }

    }        
    
}
    





