package com.example.bugtracker.exception;

public class RoleIsMissingException extends RuntimeException{
    public RoleIsMissingException(String message) {
        super(message);
}
}
