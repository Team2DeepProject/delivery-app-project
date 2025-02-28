package com.example.deliveryappproject.web.auth.controller;

import com.example.deliveryappproject.domain.auth.dto.request.AuthLoginRequest;
import com.example.deliveryappproject.domain.auth.dto.request.AuthRefreshTokenRequest;
import com.example.deliveryappproject.domain.auth.dto.response.AuthTokenResponse;
import com.example.deliveryappproject.domain.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthTokenResponse login(@Valid @RequestBody AuthLoginRequest authLoginRequest) {
        return authService.login(authLoginRequest);
    }

    @GetMapping("/logout")
    public void logout(@Auth AuthUser authUser) {
        authService.logout(authUser);
    }

    @PostMapping("/refresh")
    public AuthTokenResponse reissueAccessToken(@RequestBody AuthRefreshTokenRequest authRefreshTokenRequest) {
        return authService.reissueAccessToken(authRefreshTokenRequest);
    }
}
