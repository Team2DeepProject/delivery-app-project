package com.example.deliveryappproject.domain.cart.dto;

import com.example.deliveryappproject.domain.cart.model.CartItem;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CartItemResponse {

    private final long itemId;
    private final int quantity;


    private CartItemResponse(long itemId,int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }

    public static CartItemResponse from(CartItem cartItem) {
        return new CartItemResponse(cartItem.getItemId(), cartItem.getQuantity());
    }
}
