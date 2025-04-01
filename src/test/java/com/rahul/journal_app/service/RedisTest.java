package com.rahul.journal_app.service;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.cache.*;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
@Slf4j
public class RedisTest {

    @Autowired
    private RedisTemplate redisTemplate;


//    @Test
//    void testRedisConnection(){
//        try {
//            redisTemplate.opsForValue().set("email", "rahul@gmail.com");
//            //String salary= (String) redisTemplate.opsForValue().get("salary");
//            String email = (String) redisTemplate.opsForValue().get("email");
//            int x = 1;
//        }catch (Exception e){
//            log.error("Error: {}", e.getMessage(), e);
//        }
//    }e



}
