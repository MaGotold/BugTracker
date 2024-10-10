package com.example.bugtracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bugtracker.dto.UserRegistrationDto;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/auth")
public class AuthRegistrationController {
    
    @PostMapping("/registration")
    public String userRegistration(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        
        
    
    }
    




}
