package com.example.deliveryappproject.domain.cart.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CartItemResponse {

    private final long itemId;
    private final int quantity;


    @JsonCreator
    public CartItemResponse(@JsonProperty(value = "itemId") long itemId,@JsonProperty(value = "quantity") int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
