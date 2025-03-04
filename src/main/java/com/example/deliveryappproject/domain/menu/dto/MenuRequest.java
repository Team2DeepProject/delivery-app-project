package com.example.deliveryappproject.domain.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MenuRequest {

    private String menuName;
    private BigDecimal price;
    private String information;
    private Long storeId;

}
