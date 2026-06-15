package com.myitworld.content.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myitworld.content.config.ContentCacheProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.function.Supplier;

/**
 * 公开读接口 Redis 缓存
 */
@Service
@RequiredArgsConstructor
public class ContentCacheService {

    private static final String PREFIX = "content:cache:";

    private final StringRedisTemplate redisTemplate;
    private final ContentCacheProperties cacheProperties;
    private final ObjectMapper objectMapper;

    public <T> T getOrLoad(String key, Class<T> type, Supplier<T> loader) {
        String cacheKey = PREFIX + key;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, type);
            } catch (JsonProcessingException ignored) {
                redisTemplate.delete(cacheKey);
            }
        }
        T data = loader.get();
        writeCache(cacheKey, data);
        return data;
    }

    public <T> T getOrLoad(String key, TypeReference<T> typeRef, Supplier<T> loader) {
        String cacheKey = PREFIX + key;
        String cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, typeRef);
            } catch (JsonProcessingException ignored) {
                redisTemplate.delete(cacheKey);
            }
        }
        T data = loader.get();
        writeCache(cacheKey, data);
        return data;
    }

    private <T> void writeCache(String cacheKey, T data) {
        if (data == null) {
            return;
        }
        try {
            redisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(data),
                    Duration.ofSeconds(cacheProperties.getTtlSeconds()));
        } catch (JsonProcessingException ignored) {
            // 缓存失败不影响主流程
        }
    }

    public void evictAll() {
        var keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
