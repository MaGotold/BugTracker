package com.example.bugtracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.example.bugtracker.dto.UserRegistrationDto;
import com.example.bugtracker.dto.UserSignInDto;
import com.example.bugtracker.security.JwtUtil;
import com.example.bugtracker.service.AuthService;
import com.example.bugtracker.service.RedisService;

import org.springframework.http.HttpStatus;


import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;







@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private JwtUtil jwtUtil;
    
    //todo add email verification
    //todo add password confirmation
    //todo changed role so specifying role upon registration is needed... add role field
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, String>> userRegistration(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            Map<String, String> response = authService.registerUser(userRegistrationDto);
            return ResponseEntity.ok(response); 

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", e.getMessage() ));        }
        }


    //todo add password reset
    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> userSignIn(@Valid @RequestBody UserSignInDto userSignInDto){
        try {
            Map<String, String> response = authService.userSignIn(userSignInDto);
            return ResponseEntity.ok(response);
        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", e.getMessage() ));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> userLogout(@RequestHeader(name = "Authorization") String authHeader) {
        return ResponseEntity.ok("User logged out successfully.");
    }
}





