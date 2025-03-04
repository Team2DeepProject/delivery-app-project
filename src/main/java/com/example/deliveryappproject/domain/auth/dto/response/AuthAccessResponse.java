package com.example.deliveryappproject.domain.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthAccessResponse {

    private final String accessToken;
}
