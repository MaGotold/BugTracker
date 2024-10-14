package com.example.bugtracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;
import com.example.bugtracker.exception.SessionNotFoundException;



@Service
public class RedisService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void cacheJwtToken(String key, String token, long expirationMinutes) {

        String sessionId = UUID.randomUUID().toString();
        String hashKey = "user:sessions:" + key + ":" + sessionId;

        Map<String,String> tokenData = new HashMap<>();
        tokenData.put("token", token);
        tokenData.put("issued_at", String.valueOf(System.currentTimeMillis()));
        tokenData.put("expires", String.valueOf(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(expirationMinutes)));

        try {
            redisTemplate.opsForHash().putAll(hashKey, tokenData);
            redisTemplate.expire(hashKey, expirationMinutes, TimeUnit.MINUTES);
        } catch (org.springframework.data.redis.RedisSystemException e) {
            throw new RuntimeException("Failed to cache JWT token due to Redis error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while caching JWT token: " + e.getMessage(), e);
        }
    }


    public String identifySession(String token, String key) {
        String keyPattern = "user:sessions:" + key + ":*";
        Set<String> sessionKeys;
    
        try {
            sessionKeys = redisTemplate.keys(keyPattern);
        } catch (org.springframework.data.redis.RedisSystemException e) {
            throw new RuntimeException("Failed to retrieve session keys from Redis: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while retrieving session keys: " + e.getMessage(), e);
        }
    
        if (sessionKeys == null || sessionKeys.isEmpty()) {
            return null; 
        }
    
        for (String sessionKey : sessionKeys) {
            String storedToken;
    
            try {
                storedToken = (String) redisTemplate.opsForHash().get(sessionKey, "token");
            } catch (org.springframework.data.redis.RedisSystemException e) {
                throw new RuntimeException("Failed to retrieve token from Redis for session key: " + sessionKey + ", error: " + e.getMessage(), e);
            } catch (Exception e) {
                throw new RuntimeException("An unexpected error occurred while retrieving token from session key: " + sessionKey + ", error: " + e.getMessage(), e);
            }
    
            if (storedToken != null && storedToken.equals(token)) {
                return sessionKey; 
            }
        }
    
        return null; 
    }
    


    public void deleteSession(String token, String key) {
        
        String session = identifySession(token, key);
        
        if(session != null) {
        try {
            redisTemplate.delete(session);
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred while deleting the session: " + e.getMessage(), e);
        }
        } else {
            throw new SessionNotFoundException("No session found for the provided token.");
        }
    }
}
