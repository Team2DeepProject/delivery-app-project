package com.example.deliveryappproject.domain.cart.service;

import com.example.deliveryappproject.domain.cart.dto.CartItemResponse;
import com.example.deliveryappproject.domain.cart.dto.CartItemsRequest;
import com.example.deliveryappproject.domain.cart.dto.CartItemsSaveResponse;
import com.example.deliveryappproject.domain.cart.dto.CartResponse;
import com.example.deliveryappproject.domain.cart.repository.CartRepository;
import com.example.deliveryappproject.domain.cart.repository.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final RedisLockRepository redisLockRepository;

    public CartItemsSaveResponse addItems(Long userId, CartItemsRequest cartItemsRequest) {
        redisLockRepository.waitForLock(userId);
        try {
            Optional<Long> storeId = cartRepository.findStoreId(userId);
            if (storeId.isEmpty() || storeId.get() != cartItemsRequest.getStoreId()) {
                cartRepository.clear(userId);
            }
            cartRepository.saveItems(userId, cartItemsRequest);
        } finally {
            redisLockRepository.unLock(userId);
        }
        Map<String, String> items = cartRepository.getCartDetails(userId);
        List<CartItemResponse> cartItemResponseList = items.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("items:"))
                .map(entry -> {
                    long itemId = Long.parseLong(entry.getKey().split(":")[1]);
                    int quantity = Integer.parseInt(entry.getValue());
                    return new CartItemResponse(itemId, quantity);
                }).toList();
        CartResponse cartResponse = new CartResponse(cartItemsRequest.getStoreId(), cartItemResponseList);
        return new CartItemsSaveResponse("성공", cartResponse);
    }

    public CartResponse getItems(Long userId) {
        Long storeId = cartRepository.findStoreId(userId).orElse(null);

        Map<String, String> items = cartRepository.getCartDetails(userId);
        List<CartItemResponse> cartItemResponseList = items.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("items:"))
                .map(entry -> {
                    long itemId = Long.parseLong(entry.getKey().split(":")[1]);
                    int quantity = Integer.parseInt(entry.getValue());
                    return new CartItemResponse(itemId, quantity);
                }).toList();
        return new CartResponse(storeId, cartItemResponseList);
    }

    public void clearCart(Long userId) {
        cartRepository.clear(userId);
    }

    public void deleteCartItem(Long userId, Long itemId) {
        cartRepository.deleteItem(userId, itemId);
    }

    public void decreaseItemQuantity(Long userId, Long itemId, int quantity) {
        cartRepository.decreaseItemQuantity(userId, itemId, quantity);
    }
}
