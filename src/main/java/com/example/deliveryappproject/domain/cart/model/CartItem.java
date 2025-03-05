package com.example.deliveryappproject.domain.cart.model;


import lombok.Getter;

@Getter
public class CartItem {

    private long itemId;
    private int quantity;


    public CartItem(Long itemId,int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
