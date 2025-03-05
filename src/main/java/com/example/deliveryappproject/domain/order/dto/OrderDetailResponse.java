package com.example.deliveryappproject.domain.order.dto;

import com.example.deliveryappproject.domain.order.entity.Order;
import com.example.deliveryappproject.domain.order.entity.OrderItem;
import com.example.deliveryappproject.domain.store.dto.StoreResponse;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
public class OrderDetailResponse {

    private final Long orderId;
    private final String orderStatus;
    private final StoreResponse storeResponse;
    private final List<OrderItemResponse> orderItemResponses;
    private final BigDecimal totalPrice;
    private final int usedPoints;

    private OrderDetailResponse(Long orderId, String orderStatus, StoreResponse storeResponse, List<OrderItemResponse> orderItemResponses, BigDecimal totalPrice, int usedPoints) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.storeResponse = storeResponse;
        this.orderItemResponses = orderItemResponses;
        this.totalPrice = totalPrice;
        this.usedPoints = usedPoints;
    }

    public static OrderDetailResponse from(Order order) {
        StoreResponse storeResponse = StoreResponse.of(order.getStore().getId(), order.getStore().getStoreName());
        List<OrderItemResponse> orderItemResponses = new ArrayList<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            orderItemResponses.add(OrderItemResponse.from(orderItem));
        }
        return new OrderDetailResponse(order.getId(), order.getOrderStatus().name(), storeResponse, orderItemResponses, order.getTotalPrice(), order.getUsedPoints());
    }
}
