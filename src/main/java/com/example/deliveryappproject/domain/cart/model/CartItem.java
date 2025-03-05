package com.example.deliveryappproject.domain.cart.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class CartItem {

    private long itemId;
    private int quantity;


    @JsonCreator
    public CartItem(@JsonProperty(value = "itemId") Long itemId, @JsonProperty(value = "quantity") int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
