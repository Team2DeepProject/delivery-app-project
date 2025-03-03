package com.example.deliveryappproject.user.dto;

import com.example.deliveryappproject.user.entity.UserRole;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long userId;
    private final String email;
    private final String userName;
    private final String userRole;
    private final int point;

    public UserResponseDto(Long userId, String email, String userName, String userRole, int point) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.userRole = userRole;
        this.point = point;
    }
}
