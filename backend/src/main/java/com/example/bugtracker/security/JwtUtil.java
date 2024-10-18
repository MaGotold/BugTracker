package com.example.bugtracker.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import com.example.bugtracker.model.User;

import java.security.SecureRandom;
import java.util.Base64;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;
    
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 12;

    @Value("${JWT_EXPIRATION_MINUTES}")
    private long TTL_EXPIRATION_FOR_REDIS;


    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        return createToken(claims, user.getUsername());
    }


    public String createToken(Map<String,Object> claims, String subject) {
        Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /* 
    public static String generateSecretKey(){
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[32];
        secureRandom.nextBytes(key);
        return Base64.getEncoder().encodeToString(key);
    }
    */
    
    public long getTtlExpirationForRedis() {
        return TTL_EXPIRATION_FOR_REDIS;
    }

    public String getSecretKey() {
        return secretKey;
    }


    
}
