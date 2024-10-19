package com.example.bugtracker.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.bugtracker.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;


@Component
public class JwtUtil {

    @Value("${JWT_SECRET_KEY}")
    private String secretKey;
    
    private final long EXPIRATION_TIME_ACCESS_TOKEN = 1000 * 60 * 15;
    private final long EXPIRATION_TIME_REFRESH_TOKEN = 1000 * 60 * 60 * 12;

    @Value("${JWT_EXPIRATION_MINUTES}")
    private long TTL_EXPIRATION_FOR_REDIS;


    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("role", user.getRole().name());
        return createToken(claims, user.getUsername(), EXPIRATION_TIME_ACCESS_TOKEN);
    }
    

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id",user.getId());
        return createToken(claims, user.getUsername(), EXPIRATION_TIME_REFRESH_TOKEN);
    }


    public String createToken(Map<String,Object> claims, String subject, long expirationTime) {
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);
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
