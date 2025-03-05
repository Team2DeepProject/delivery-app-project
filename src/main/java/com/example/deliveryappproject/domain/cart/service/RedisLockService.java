package com.example.deliveryappproject.domain.cart.service;

import com.example.deliveryappproject.domain.cart.repository.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisLockService {

    private final RedisLockRepository redisLockRepository;

    public void executeWithLock(Long key, Runnable runnable) {

        redisLockRepository.waitForLock(key);
        try {
            runnable.run();
        } finally {
            redisLockRepository.unLock(key);
        }

    }
}
