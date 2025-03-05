package com.example.deliveryappproject.domain.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CartItemRequest {

    @Min(1)
    private long itemId;
    @Min(1)
    private int quantity;
}
