package com.example.bugtracker.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bugtracker.dto.RefreshTokenDto;
import com.example.bugtracker.dto.UserRegistrationDto;
import com.example.bugtracker.dto.UserSignInDto;
import com.example.bugtracker.exception.TokenInvalidException;
import com.example.bugtracker.exception.UserNotFoundException;
import com.example.bugtracker.service.AuthService;

import jakarta.validation.Valid;







@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    
    //todo add email verification
    //todo add password confirmation
    //todo change role so specifying role upon registration is needed... add role field
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
    public ResponseEntity<?> userLogout(@RequestBody Map<String, String> token) {
        try {
            String refreshToken = token.get("Refresh JWT token");
            authService.userLogout(refreshToken);
            return ResponseEntity.ok("User logged out successfully.");

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred: " + e.getMessage());
        }
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authHeader, @RequestBody RefreshTokenDto username) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Refresh token is missing or invalid.");
        }
        String token = authHeader.substring(7);
        try {
            Map<String, String> response = authService.refreshToken(token, username);
            return ResponseEntity.ok(response);

        } catch (TokenInvalidException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());

        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred: " + e.getMessage());
        }
    }
}





