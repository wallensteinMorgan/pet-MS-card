package com.example.demo.service.impl;

import com.example.demo.dto.CardDTO;
import com.example.demo.exception.RedisOperationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
            log.debug("Cached value for key: {} with TTL: {} {}", key, ttl, timeUnit);
        } catch (Exception e) {
            log.error("Redis set operation failed for key: {}", key, e);
            throw new RedisOperationException("Failed to set value in Redis", e);
        }
    }
    public Object get(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            log.debug("Retrieved value for key: {}", key);
            return value;
        } catch (Exception e) {
            log.error("Redis get operation failed for key: {}", key, e);
            throw new RedisOperationException("Failed to get value from Redis", e);
        }
    }
    public void delete(String key) {
        try {
            Boolean deleted = redisTemplate.delete(key);
            log.debug("Deleted key: {}, success: {}", key, deleted);
        } catch (Exception e) {
            log.error("Redis delete operation failed for key: {}", key, e);
            throw new RedisOperationException("Failed to delete key in Redis", e);
        }
    }
    public void cacheCard(Long cardId, CardDTO cardDto) {
        String key = String.format("card:details:%d", cardId);
        set(key, cardDto, 1, TimeUnit.HOURS); // 1 час
    }
    public CardDTO getCachedCard(Long cardId) {
        String key = String.format("card:details:%d", cardId);
        try {
            return (CardDTO) get(key);
        } catch (ClassCastException e) {
            log.error("Invalid cache data format for card: {}", cardId, e);
            delete(key);
            return null;
        }
    }
}