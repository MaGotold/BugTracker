package com.example.bugtracker.unit.service;

import com.example.bugtracker.exception.EmailAlreadyExistsException;
import com.example.bugtracker.exception.InvalidPasswordException;
import com.example.bugtracker.exception.UserAlreadyExistsException;
import com.example.bugtracker.exception.UserNotFoundException;
import com.example.bugtracker.repository.UserRepository;
import com.example.bugtracker.service.AuthService;
import com.example.bugtracker.dto.UserRegistrationDto;
import com.example.bugtracker.dto.UserSignInDto;
import com.example.bugtracker.model.User;
import com.example.bugtracker.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private UserRegistrationDto registrationDto;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
        registrationDto = new UserRegistrationDto();
        registrationDto.setUsername("testUser");
        registrationDto.setEmail("test@example.com");
        registrationDto.setPassword("Strongcverypassword123");
    }


    //registerUser Method
    @Test
    void registerUser_UsernameAlreadyExists_TheowsUserALreadyExistsException() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        Exception exception = assertThrows(UserAlreadyExistsException.class,() ->{
            authService.registerUser(registrationDto);
        });
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsEmailAlreadyExistsException() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        Exception exception = assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.registerUser(registrationDto);
        });

        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void registerUser_SuccessfulRegistration_ReturnsJwtToken() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("some.jwt.token");

        String token = authService.registerUser(registrationDto);

        assertNotNull(token);
        assertEquals("some.jwt.token", token);
        verify(userRepository).save(any(User.class)); 
    }




    //signIn Method
    @Test
    void userSignIn_UserNotFound_ThrowsUserNotFoundException() {
        UserSignInDto signInDto = new UserSignInDto();
        signInDto.setUsernameOrEmail("nonexistent@example.com");
        signInDto.setPassword("anyPassword");

        when(userRepository.existsByEmail(signInDto.getUsernameOrEmail())).thenReturn(false);
        when(userRepository.existsByUsername(signInDto.getUsernameOrEmail())).thenReturn(false);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            authService.userSignIn(signInDto);
        });

        assertEquals("Username or Email doesn't exist", exception.getMessage());
    }


    @Test
    void userSignIn_InvalidPassword_ThrowsInvalidPasswordException() {
        UserSignInDto signInDto = new UserSignInDto();
        signInDto.setUsernameOrEmail("user@example.com");
        signInDto.setPassword("wrongpassword");
    
        when(userRepository.existsByEmail(signInDto.getUsernameOrEmail())).thenReturn(true);
        when(userRepository.existsByUsername(signInDto.getUsernameOrEmail())).thenReturn(false);
    
        String storedHashedPassword = bCryptPasswordEncoder.encode("correctpassword");
        when(userRepository.findHasedPasswordByUsernameOrEmail(signInDto.getUsernameOrEmail())).thenReturn(storedHashedPassword);
        when(bCryptPasswordEncoder.matches(signInDto.getPassword(), storedHashedPassword)).thenReturn(false);
    
        Exception exception = assertThrows(InvalidPasswordException.class, () -> {
            authService.userSignIn(signInDto);
        });
    
        assertEquals("Invalid password", exception.getMessage());
    }
    
/* 
    @Test
    void userSignIn_SuccessfulLogin_ReturnsJwtToken() {
        UserSignInDto signInDto = new UserSignInDto();
        signInDto.setUsernameOrEmail("user@example.com");
        signInDto.setPassword("password123");

        when(userRepository.existsByEmail(signInDto.getUsernameOrEmail())).thenReturn(true);
        when(userRepository.existsByUsername(signInDto.getUsernameOrEmail())).thenReturn(false);

        String storedHashedPassword = bCryptPasswordEncoder.encode(signInDto.getPassword());
        when(userRepository.findHasedPasswordByUsernameOrEmail(signInDto.getUsernameOrEmail())).thenReturn(storedHashedPassword);
        when(bCryptPasswordEncoder.matches(signInDto.getPassword(), storedHashedPassword)).thenReturn(true);

        String jwtToken = "jwtToken";
        when(jwtUtil.generateToken(any())).thenReturn(jwtToken);

        String result = authService.userSignIn(signInDto);

        assertEquals(jwtToken, result);
    }

*/

}