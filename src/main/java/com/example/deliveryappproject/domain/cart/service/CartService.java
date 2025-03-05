package com.example.deliveryappproject.domain.cart.service;

import com.example.deliveryappproject.domain.cart.dto.CartItemResponse;
import com.example.deliveryappproject.domain.cart.dto.CartItemsRequest;
import com.example.deliveryappproject.domain.cart.dto.CartResponse;
import com.example.deliveryappproject.domain.cart.model.CartItem;
import com.example.deliveryappproject.domain.cart.repository.CartRepository;
import com.example.deliveryappproject.domain.cart.repository.RedisLockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final RedisLockService redisLockService;

    public void addItems(Long userId, CartItemsRequest cartItemsRequest) {

        redisLockService.executeWithLock(userId, () -> {
            Long storeId = cartRepository.findStoreId(userId);

            if (storeId == null  || storeId != cartItemsRequest.getStoreId()) {
                cartRepository.clear(userId);
            }

            cartRepository.saveItems(userId, cartItemsRequest);
        });

    }

    public CartResponse getItems(Long userId) {
        Long storeId = cartRepository.findStoreId(userId);
        List<CartItem> cartItems = cartRepository.findItems(userId);

        List<CartItemResponse> cartItemResponses = cartItems.stream().map(CartItemResponse::from).toList();
        return new CartResponse(storeId, cartItemResponses);
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
