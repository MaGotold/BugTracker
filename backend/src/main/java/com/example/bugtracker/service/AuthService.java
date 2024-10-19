package com.example.bugtracker.service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.bugtracker.dto.UserRegistrationDto;
import com.example.bugtracker.dto.UserSignInDto;
import com.example.bugtracker.exception.EmailAlreadyExistsException;
import com.example.bugtracker.exception.InvalidPasswordException;
import com.example.bugtracker.exception.RoleIsMissingException;
import com.example.bugtracker.exception.UserAlreadyExistsException;
import com.example.bugtracker.exception.UserNotFoundException;
import com.example.bugtracker.model.User;
import com.example.bugtracker.model.enums.Role;
import com.example.bugtracker.repository.UserRepository;
import com.example.bugtracker.security.JwtUtil;




@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private RedisService redisService;
   

    public Map<String, String> registerUser(UserRegistrationDto registrationDto) {

        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        if(userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        if(registrationDto.getRole() == null){
            throw new RoleIsMissingException("Please select your role");
        }

        try {
            String role = registrationDto.getRole().name();
            Role roleEnum = Role.valueOf(role);
            User newUser = new User();
            newUser.setUsername(registrationDto.getUsername());
            newUser.setEmail(registrationDto.getEmail());
            newUser.setPassword(bCryptPasswordEncoder.encode(registrationDto.getPassword()));
            newUser.setRole(roleEnum);
            userRepository.save(newUser);

            Map<String, String> response = new HashMap<>();
            response.put("Access JWT token", jwtUtil.generateAccessToken(newUser));
            response.put("Refresh JWT token", jwtUtil.generateRefreshToken(newUser));

            redisService.cacheJwtToken(newUser.getUsername(), response.get("Refresh JWT token"), jwtUtil.getTtlExpirationForRedis());
            this.setSecurityContext(newUser);

            return response;
            
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while registering the user: " + e.getMessage(), e);
        }
    }


    public Map<String, String> userSignIn(UserSignInDto signInDto){

            if(userRepository.existsByEmail(signInDto.getUsernameOrEmail()) == false 
                & userRepository.existsByUsername(signInDto.getUsernameOrEmail()) == false) {
                    throw new UserNotFoundException("Username or Email doesn't exist");
            }

            String storedHashedPassword = userRepository.findHasedPasswordByUsernameOrEmail(signInDto.getUsernameOrEmail());
            if(!bCryptPasswordEncoder.matches(signInDto.getPassword(), storedHashedPassword)) {
                throw new InvalidPasswordException("Invalid password");

            }

            User loggedUser = userRepository.findByUsername(signInDto.getUsernameOrEmail());
            Map<String, String> response = new HashMap<>();
            response.put("Access JWT token", jwtUtil.generateAccessToken(loggedUser));
            response.put("Refresh JWT token", jwtUtil.generateRefreshToken(loggedUser));

            redisService.cacheJwtToken(loggedUser.getUsername(), response.get("Refresh JWT token"), jwtUtil.getTtlExpirationForRedis());
            this.setSecurityContext(loggedUser);

            return response;

    }


    private void setSecurityContext(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name()));
        UsernamePasswordAuthenticationToken authentication = 
            new UsernamePasswordAuthenticationToken(user.getUsername(), null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    public void userLogout(String token) {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                
                if (authentication == null) {
                    throw new IllegalStateException("User is not authenticated.");
                }
        
                String subject = authentication.getName();
                if (subject == null) {
                    throw new IllegalStateException("User information not found in Security Context.");
                }
        
                redisService.deleteSession(token, subject); // Invalidate token from Redis
                SecurityContextHolder.clearContext();  // Clear Security Context for the current request
        
            } catch (NullPointerException e) {
                throw new IllegalStateException("Error during logout: Authentication details missing.", e);
            } catch (Exception e) {
                throw new RuntimeException("An unexpected error occurred while logging out: " + e.getMessage(), e);
            }
        }
        
}
    


