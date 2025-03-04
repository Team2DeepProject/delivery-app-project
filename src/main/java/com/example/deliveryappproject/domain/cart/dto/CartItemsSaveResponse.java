package com.example.deliveryappproject.domain.cart.dto;

import lombok.Getter;

import javax.management.monitor.StringMonitor;
import java.util.Map;

@Getter
public class CartItemsSaveResponse {

    private final String message;
    private final CartResponse cart;

    public CartItemsSaveResponse(String message, CartResponse cart) {
        this.message = message;
        this.cart = cart;
    }
}
