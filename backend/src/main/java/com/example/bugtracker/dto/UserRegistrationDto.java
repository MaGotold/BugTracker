package com.example.bugtracker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class UserRegistrationDto {
    
    @NotBlank(message = "Username is required")
    private String username;


    @NotBlank(message = "Email is required")
    @Email(message = "Email needs to be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Min(value = 8, message = "Password must be at least 8 characters long")
    private String password;


    public String getUsername(){
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
