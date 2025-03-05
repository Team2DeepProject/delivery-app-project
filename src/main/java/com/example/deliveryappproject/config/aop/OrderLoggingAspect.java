package com.example.deliveryappproject.config.aop;

import com.example.deliveryappproject.common.response.DefaultResponse;
import com.example.deliveryappproject.domain.delivery.dto.DeliveryResponse;
import com.example.deliveryappproject.domain.order.service.dto.OrderResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@RequiredArgsConstructor
@Component
@Slf4j
public class OrderLoggingAspect {

    private final HttpServletRequest request;

    @Around("@annotation(com.example.deliveryappproject.config.aop.annotation.OrderLogging)")
    public Object logOrder(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        String url = request.getRequestURI();
        LocalDateTime requestTime = LocalDateTime.now();
        Long orderId = null;
        for (int i = 0; i < parameterNames.length; i++) {
            System.out.println(parameterNames[i]);
            if ("orderId".equals(parameterNames[i]) && args[i] instanceof Long) {
                orderId = (Long) args[i];
            }
        }
        log.info("주문 요청: 주문 ID={}, URL={}, 요청 시각={}", orderId, url, requestTime);

        Object storeId = null;
        Object orderStatus = null;
        Object result = joinPoint.proceed();
        if (result instanceof DefaultResponse<?> defaultResponse) {
            Object data = defaultResponse.getData();

            if (data instanceof OrderResponse orderResponse) {
                storeId = orderResponse.getOrderId();
                orderStatus = orderResponse.getOrderStatus();
                orderId = orderResponse.getOrderId();
            }

            if (data instanceof DeliveryResponse deliveryResponse) {
                storeId = deliveryResponse.getStoreId();
                orderStatus = deliveryResponse.getOrderStatus();
                orderId = deliveryResponse.getOrderId();
            }
        }

        LocalDateTime responseTime = LocalDateTime.now();
        log.info("주문 응답: 주문 ID={}, 가게 ID={},주문 상태={}, URL={}, 요청 시각={}", orderId, storeId,orderStatus,  url, responseTime);

        return result;
    }
}
