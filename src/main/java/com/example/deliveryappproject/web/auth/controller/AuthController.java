package com.example.deliveryappproject.web.auth.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.RefreshToken;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.auth.dto.request.AuthLoginRequest;
import com.example.deliveryappproject.domain.auth.dto.response.AuthTokenResponse;
import com.example.deliveryappproject.domain.auth.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthTokenResponse login(@Valid @RequestBody AuthLoginRequest authLoginRequest, HttpServletResponse response) {
        AuthTokenResponse authTokenResponse = authService.login(authLoginRequest);
        setRefreshTokenCookie(response, authTokenResponse.getRefreshToken());
        return authTokenResponse;
    }

    @GetMapping("/logout")
    public void logout(@Auth AuthUser authUser) {
        authService.logout(authUser);
    }

    @GetMapping("/refresh")
    public void reissueAccessToken(@RefreshToken String refreshToken, HttpServletResponse response) {
        AuthTokenResponse authTokenResponse = authService.reissueAccessToken(refreshToken);
        setRefreshTokenCookie(response, authTokenResponse.getRefreshToken());
    }

    private void setRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
