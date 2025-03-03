package com.example.deliveryappproject.domain.menu.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MenuRequest {

    private String menuName;
    private BigDecimal price;
    private String information;
    private Long storeId;

}
