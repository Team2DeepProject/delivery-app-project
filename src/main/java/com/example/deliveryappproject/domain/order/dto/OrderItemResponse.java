package com.example.deliveryappproject.domain.order.dto;

import com.example.deliveryappproject.domain.order.entity.OrderItem;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class OrderItemResponse {
    private final Long orderItemId;
    private final String menuName;
    private final int quantity;
    private final BigDecimal price;

    private OrderItemResponse(Long orderItemId, String menuName, int quantity, BigDecimal price) {
        this.orderItemId = orderItemId;
        this.menuName = menuName;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemResponse from(OrderItem orderItem) {
        return new OrderItemResponse(orderItem.getId(), orderItem.getMenuName(), orderItem.getQuantity(), orderItem.getOrderPrice());
    }
}
