package com.example.deliveryappproject.domain.cart.repository;

import com.example.deliveryappproject.domain.cart.dto.CartItemRequest;
import com.example.deliveryappproject.domain.cart.dto.CartItemsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CartRepository {

    private final RedisTemplate<String, String> redisTemplate;
    private static final long durationTime = 5L;

    public void saveItems(Long userId, CartItemsRequest cartItemsRequest) {
        String cartKey = getCartKey(userId);
        long storeId = cartItemsRequest.getStoreId();
        redisTemplate.opsForHash().put(cartKey, "storeId", String.valueOf(storeId));
        for (CartItemRequest item : cartItemsRequest.getItems()) {
            String itemKey = getItemKey(item.getItemId());
            redisTemplate.opsForHash().increment(cartKey, itemKey, item.getQuantity());
        }
        redisTemplate.expire(cartKey, Duration.ofMinutes(durationTime));
    }

    public Map<String, String> getCartDetails(Long userId) {
        String cartKey = getCartKey(userId);
        return redisTemplate.<String, String>opsForHash().entries(cartKey);
    }

    public Optional<Long> findStoreId(long userId) {
        String cartKey = getCartKey(userId);
        String storeId = redisTemplate.<String, String>opsForHash().get(cartKey, "storeId");
        return storeId != null ? Optional.of(Long.parseLong(storeId)) : Optional.empty();
    }



    public void clear(Long userId) {
        String cartKey = getCartKey(userId);
        redisTemplate.delete(cartKey);
    }

    public void deleteItem(Long userId, Long itemId) {
        String cartKey = getCartKey(userId);
        String itemKey = getItemKey(itemId);
        redisTemplate.opsForHash().delete(cartKey, itemKey);
    }

    public void decreaseItemQuantity(Long userId, Long itemId, int quantity) {
        String cartKey = getCartKey(userId);
        String itemKey = getItemKey(itemId);
        Long newQuantity = redisTemplate.opsForHash().increment(cartKey, itemKey, -quantity);

        if (newQuantity <= 0) {
            redisTemplate.opsForHash().delete(cartKey, itemKey);
        }
        redisTemplate.expire(cartKey, Duration.ofMinutes(durationTime));
    }

    private String getCartKey(long userId) {
        return "cart:users:" + userId;
    }

    private String getItemKey(Long itemId) {
        return "items:" + itemId;
    }

}
