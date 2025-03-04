package com.example.deliveryappproject.domain.cart.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CartResponse {

    private final Long storeId;
    private final List<CartItemResponse> items;

    public CartResponse(Long storeId, List<CartItemResponse> items) {
        this.storeId = storeId;
        this.items = items;
    }
}
