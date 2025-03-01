package com.example.deliveryappproject.domain.user.dto.response;

import com.example.deliveryappproject.domain.user.entity.UserRole;
import lombok.Getter;

@Getter
public class UserSignupResponseDto {
    private final Long id;  // userId에서 id로 수정
    private final String email;
    private final String userName;
    private final UserRole userRole;
    private final int point;

    public UserSignupResponseDto(Long id, String email, String userName, UserRole userRole, int point) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.userRole = userRole;
        this.point = point;
    }
}
