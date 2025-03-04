package com.example.deliveryappproject.domain.user.dto.response;

import lombok.Getter;

@Getter
public class UserUpdateResponse {
    private final String userName;
    private final int point;
    private final String userRole;

    public UserUpdateResponse(String userName, int point, String userRole) {
        this.userName = userName;
        this.point = point;
        this.userRole = userRole;
    }
}
