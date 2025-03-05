package com.example.deliveryappproject.domain.cart.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.cart.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartReader {
    private final CartRepository cartRepository;

    public Optional<Long> readStoreId(Long userId) {
        Object storeId = cartRepository.findStoreId(userId);
        if (storeId == null) {
            return Optional.empty();
        }
        return Optional.of(Long.parseLong((String) storeId));
    }

}
