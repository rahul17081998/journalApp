package com.rahul.journal_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {

    @Autowired
    private RedisTemplate redisTemplate;

    public <T> T get(String key, Class<T> entityClass){
        try {
            Object o = redisTemplate.opsForValue().get(key);
            if(o==null){
                log.warn("No value found for key: {}", key);
                return null;
            }
            ObjectMapper objectMapper = new ObjectMapper();
            T result=objectMapper.readValue(o.toString(), entityClass);
            log.info("Successfully retrieved and deserialized value for key: {}", key);
            return result;
        }catch (Exception e){
            log.error("Error occurred while retrieving or deserializing value for key: {}", key, e);
            return null;
        }
    }

    public void set(String key, Object object, Long ttl){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String json=objectMapper.writeValueAsString(object);
            redisTemplate.opsForValue().set(key, json, ttl, TimeUnit.SECONDS);
            log.info("Successfully set value for key: {} with expiry time: {} seconds", key, ttl);
        }catch (Exception e){
            log.error("Error occurred while setting value for key: {}", key, e);
        }
    }
}
