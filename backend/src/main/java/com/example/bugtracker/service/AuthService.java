package com.example.bugtracker.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
            response.put("username", loggedUser.getUsername());
            return response;

    }
    
}

