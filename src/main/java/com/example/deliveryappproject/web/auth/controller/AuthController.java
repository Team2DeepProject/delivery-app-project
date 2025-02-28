package com.example.deliveryappproject.web.auth.controller;

import com.example.deliveryappproject.domain.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auths")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
}
