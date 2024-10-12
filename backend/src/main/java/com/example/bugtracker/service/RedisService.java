package com.example.bugtracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;


@Service
public class RedisService {
    
    @Autowired
    private StringRedisTemplate redisTemplate;

    public void cacheJwtToken(String key, String token, long expirationMinutes) {
        redisTemplate.opsForValue().set(key, token, expirationMinutes, TimeUnit.MINUTES);
    }
}
