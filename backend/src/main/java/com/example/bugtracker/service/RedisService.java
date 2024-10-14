package com.example.bugtracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;


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

        redisTemplate.opsForHash().putAll(hashKey, tokenData);
        redisTemplate.expire(hashKey, expirationMinutes, TimeUnit.MINUTES);
    }


  /*   public boolean tokenExists(String token) {

        Set<String> keys = redisTemplate.keys("*");

    }*/
}
