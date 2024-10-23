package com.example.bugtracker.security;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.example.bugtracker.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;


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


    private Claims getClaims(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(this.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    
        } catch (ExpiredJwtException e) {
            System.err.println("Error: Token has expired.");
            return null; 

        } catch (SignatureException e) {
            System.err.println("Error: Invalid token signature.");
            return null; 
    
        } catch (JwtException e) {
            System.err.println("Error: Invalid token.");
            return null; 
    
        } catch (Exception e) {
            System.err.println("Error: An unexpected error occurred while parsing the token: " + e.getMessage());
            return null; 
        }
    }   
    


    public Map<String, String> parseSubjectAndRole(String token) {
        Claims claims = this.getClaims(token);
        Map<String, String> response = new HashMap<>();

        if(claims == null) {
            System.out.println("Failed to retrieve claims from token.");

        } else {
            response.put("subject",claims.getSubject());
            response.put("role", claims.get("role", String.class));
            return response;
        }
        return null;
    }   


    public Date parseExpiration(String token) {
        Claims claims = this.getClaims(token);
        if(claims == null){
            System.out.println("Failed to retrieve claims from token.");
            return null;
        } 
        return claims.getExpiration();
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
