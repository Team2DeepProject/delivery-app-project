package com.example.deliveryappproject.domain.order.exception;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class ErrorMessages {

    public static final String ORDER_NOT_AVAILABLE = "주문 가능한 시간이 아닙니다.";
    public static final String POINT_NOT_ENOUGH = "포인트가 부족합니다.";
    public static final String MIN_ORDER_AMOUNT_REQUIRED = "최소 주문 금액을 맞춰주세요.";

    public static final String ORDER_NOT_FOUND = "주문을 찾을 수 없습니다.";
    public static final String ORDER_NOT_OWNER = "해당 주문에 대한 접근 권한이 없습니다.";
    public static final String ORDER_STATUS_NOT_PENDING = "Order status is not PENDING";

}
