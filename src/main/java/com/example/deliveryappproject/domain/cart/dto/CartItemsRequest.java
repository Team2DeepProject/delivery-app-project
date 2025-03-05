package com.example.deliveryappproject.domain.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class CartItemsRequest {
    @Min(1)
    private long storeId;

    @NotEmpty(message = "장바구니 아이템은 최소 1개 이상이어야 합니다.")
    private List<@Valid CartItemRequest> items;



}
