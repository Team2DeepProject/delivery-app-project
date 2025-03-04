package com.example.deliveryappproject.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;  // userId에서 id로 수정
    private final String email;
    private final String userName;
    private final String userRole;
    private final int point;

    public UserResponse(Long id, String email, String userName, String userRole, int point) {
        this.id = id;
        this.email = email;
        this.userName = userName;
        this.userRole = userRole;
        this.point = point;
    }
}
