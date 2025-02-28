package com.example.deliveryappproject.user.entity;

import com.example.deliveryappproject.user.Exception.InvalidRequestException;

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
                .orElseThrow(() -> new InvalidRequestException("유효하지 않은 UerRole"));
    }
}
