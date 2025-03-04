package com.example.deliveryappproject.domain.cart.service;

import com.example.deliveryappproject.domain.cart.dto.CartItemRequest;
import com.example.deliveryappproject.domain.cart.dto.CartItemsRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartServiceTest {

    @Autowired
    private CartService cartService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Test
    @Transactional
    public void testConcurrentStoreIdUpdate() throws InterruptedException {

        int threadCount = 3;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        final long userId = 1L;
        long itemId = 1L;
        Runnable task1 = () -> {
            try {
                CartItemsRequest cartItemsRequest = new CartItemsRequest();
                cartItemsRequest.setStoreId(100L);
                CartItemRequest cartItemRequest = new CartItemRequest();
                cartItemRequest.setItemId(itemId);
                cartItemRequest.setQuantity(1);
                cartItemsRequest.setItems(List.of(cartItemRequest));

                cartService.addItems(userId,  cartItemsRequest);
            }finally {
                countDownLatch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                CartItemsRequest cartItemsRequest = new CartItemsRequest();
                cartItemsRequest.setStoreId(100L);
                CartItemRequest cartItemRequest = new CartItemRequest();
                cartItemRequest.setItemId(itemId);
                cartItemRequest.setQuantity(1);
                cartItemsRequest.setItems(List.of(cartItemRequest));
                cartService.addItems(userId,  cartItemsRequest);
            }finally {
                countDownLatch.countDown();
            }
        };

        Runnable task3 = () -> {
            try {
                CartItemsRequest cartItemsRequest = new CartItemsRequest();
                cartItemsRequest.setStoreId(200L);
                CartItemRequest cartItemRequest = new CartItemRequest();
                cartItemRequest.setItemId(itemId);
                cartItemRequest.setQuantity(1);
                cartItemsRequest.setItems(List.of(cartItemRequest));
                cartService.addItems(userId,  cartItemsRequest);
            }finally {
                countDownLatch.countDown();
            }
        };

        executorService.execute(task1);
        executorService.execute(task2);
        executorService.execute(task3);

        countDownLatch.await();
        executorService.shutdown();

        String cartKey = "cart:users:" + userId;
        String quantityKey = "items:" + itemId;
        int quantity = Integer.parseInt(redisTemplate.opsForHash().get(cartKey, quantityKey).toString());
        Assertions.assertEquals(3, quantity);
    }
}