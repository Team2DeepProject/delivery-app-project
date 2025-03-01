package com.example.deliveryappproject.user.dto;

import com.example.deliveryappproject.user.entity.UserRole;
import lombok.Getter;

@Getter
public class UserSignupResponseDto {
    private final Long userId;
    private final String email;
    private final String userName;
    private final UserRole userRole;
    private final int point;

    public UserSignupResponseDto(Long userId, String email, String userName, UserRole userRole, int point) {
        this.userId = userId;
        this.email = email;
        this.userName = userName;
        this.userRole = userRole;
        this.point = point;
    }
}
