package com.example.deliveryappproject.domain.delivery.dto;

import com.example.deliveryappproject.domain.order.entity.OrderStatus;
import lombok.Getter;

@Getter
public class DeliveryResponse {
    private final Long orderId;
    private final Long storeId;
    private final String orderStatus;

    private DeliveryResponse(Long orderId, Long storeId, String orderStatus) {
        this.orderId = orderId;
        this.storeId = storeId;
        this.orderStatus = orderStatus;
    }

    public static DeliveryResponse of(Long orderId, Long storeId, OrderStatus orderStatus) {
        return new DeliveryResponse(orderId, storeId, orderStatus.name());
    }
}
