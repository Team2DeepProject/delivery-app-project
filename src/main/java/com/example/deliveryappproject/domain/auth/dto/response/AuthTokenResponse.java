package com.example.deliveryappproject.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenResponse {

    private final String accessToken;
    private final String refreshToken;

}
