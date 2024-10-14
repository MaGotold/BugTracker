package com.example.bugtracker.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import com.example.bugtracker.dto.UserLogoutDto;
import com.example.bugtracker.dto.UserRegistrationDto;
import com.example.bugtracker.dto.UserSignInDto;
import com.example.bugtracker.security.JwtUtil;

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

import com.example.bugtracker.service.AuthService;
import com.example.bugtracker.service.RedisService;





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
    @PostMapping("/sign-up")
    public ResponseEntity<Map<String, String>> userRegistration(@Valid @RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            String token = authService.registerUser(userRegistrationDto);
            long expirationMinutes = jwtUtil.getTtlExpirationForRedis();
            redisService.cacheJwtToken(userRegistrationDto.getUsername(), token, expirationMinutes);
            Map<String, String> response = new HashMap<>();
            response.put("JWT token", token);
            return ResponseEntity.ok(response); 

        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", e.getMessage() ));        }
        }


    //todo add password reset
    @PostMapping("/sign-in")
    public ResponseEntity<Map<String, String>> userSignIn(@Valid @RequestBody UserSignInDto userSignInDto){
        try {
            Map<String, String> userInfo = authService.userSignIn(userSignInDto);
            String username = userInfo.get("username");
            String token = userInfo.get("token");
            long expirationMinutes = jwtUtil.getTtlExpirationForRedis();
            redisService.cacheJwtToken(username, token, expirationMinutes );
            return ResponseEntity.ok(userInfo);
        
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", e.getMessage() ));
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> userLogout(@RequestHeader(name = "Authorization") String authHeader, @RequestBody UserLogoutDto userLogoutDto) {
        try{
            String token = authHeader.substring(7);
            redisService.deleteSession(token, userLogoutDto.getUsername());

            return ResponseEntity.ok("User logged out succesfully");
        } catch(Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Collections.singletonMap("error", e.getMessage() ));
        }
    }
}





