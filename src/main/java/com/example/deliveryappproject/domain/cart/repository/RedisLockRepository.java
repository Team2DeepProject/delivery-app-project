package com.example.deliveryappproject.domain.cart.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@RequiredArgsConstructor
@Repository
public class RedisLockRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long DEFAULT_LOCK_TIMEOUT = 5L;
    private static final long RETRY_INTERVAL = 10L;


    /**
     * 락 획득 (NX 옵션)
     */
    public Boolean lock(Long key) {
        return redisTemplate
                .opsForValue()
                .setIfAbsent(generated(key), "lock", Duration.ofMillis(DEFAULT_LOCK_TIMEOUT));
    }

    public void waitForLock(Long key) {
        while (!lock(key)) {
            try {
                Thread.sleep(RETRY_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public Boolean unLock(Long key){
        return redisTemplate.delete(generated(key));
    }

    private String generated(Long key) {
        return key.toString();
    }

}
