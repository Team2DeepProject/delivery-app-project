package com.example.deliveryappproject.domain.policy;

import java.math.BigDecimal;

public interface PointPolicy {

    int calculateEarnedPoints(BigDecimal totalPrice);
}
