package com.example.bugtracker.dto;

import jakarta.validation.constraints.NotBlank;

public class UserLogoutDto {
    @NotBlank(message = "username must be provided")
    private String username;

    public String getUsername(){
        return username;
    }
}
