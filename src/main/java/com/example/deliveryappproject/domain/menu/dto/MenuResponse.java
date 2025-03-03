package com.example.deliveryappproject.domain.menu.dto;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class MenuResponse {

    private final Long id;
    private final String menuName;
    private final BigDecimal price;
    private final String information;

    public MenuResponse(Long id, String menuName, BigDecimal price, String information) {
        this.id = id;
        this.menuName = menuName;
        this.price = price;
        this.information = information;
    }
}
