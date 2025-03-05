package com.example.deliveryappproject.domain.order.service.dto;

import com.example.deliveryappproject.domain.order.entity.Order;
import com.example.deliveryappproject.domain.order.entity.OrderStatus;
import lombok.Getter;


@Getter
public class OrderResponse {

    private final Long orderId;
    private final Long storeId;
    private final String orderStatus;

    public OrderResponse(Long orderId, Long storeId, String orderStatus) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.orderStatus = orderStatus;
    }


    public static OrderResponse of(Long orderId, Long storeId, OrderStatus orderStatus) {
        return new OrderResponse(orderId, storeId,orderStatus.name());
    }

}
