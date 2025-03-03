package com.example.deliveryappproject.domain.menu.dto;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public class MenuResponse {

    private final Long id;
    private final String menuName;
    private final BigDecimal price;
    private final String information;
    private final String menuState;
    private final String storeName;

    public MenuResponse(Long id, String menuName, BigDecimal price, String information, String menuState, String storeName) {
        this.id = id;
        this.menuName = menuName;
        this.price = price.setScale(0, RoundingMode.FLOOR);
        this.information = information;
        this.menuState = menuState;
        this.storeName = storeName;
    }
}
