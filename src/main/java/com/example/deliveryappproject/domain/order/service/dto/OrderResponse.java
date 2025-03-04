package com.example.deliveryappproject.domain.order.service.dto;

import com.example.deliveryappproject.domain.order.entity.Order;
import com.example.deliveryappproject.domain.order.entity.OrderStatus;


public class OrderResponse {

    private final Long orderId;
    private final String orderStatus;
    private final String message;

    public OrderResponse(Long orderId, String orderStatus, String message) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.message = message;
    }


    public static OrderResponse of(Long orderId, OrderStatus orderStatus) {
        return new OrderResponse(orderId,orderStatus.name(),"주문 요청이 완료되었습니다. 가게에서 주문을 확인 중입니다.");
    }

}
