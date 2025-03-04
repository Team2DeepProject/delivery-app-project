package com.example.deliveryappproject.domain.user.enums;

import com.example.deliveryappproject.common.exception.BadRequestException;

import java.util.Arrays;

public enum UserRole {
    OWNER,
    USER,
    ADMIN,
    RIDER;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("유효하지 않은 UerRole"));
    }
}
