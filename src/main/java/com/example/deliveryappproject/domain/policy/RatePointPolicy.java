package com.example.deliveryappproject.domain.policy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class RatePointPolicy implements PointPolicy{

    private static final double earnRate = 0.03;

    @Override
    public int calculateEarnedPoints(BigDecimal totalPrice) {
        return totalPrice.multiply(BigDecimal.valueOf(earnRate)).intValue();
    }
}
