package com.example.bugtracker.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshTokenDto {
    @NotBlank(message = "Username is required")
    private String username;

    public String getUsername(){
        return username;
    }
}
