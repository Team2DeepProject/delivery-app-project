package com.example.deliveryappproject.user.controller;

import com.example.deliveryappproject.user.dto.UserSignupRequestDto;
import com.example.deliveryappproject.user.dto.UserSignupResponseDto;
import com.example.deliveryappproject.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signup(@Valid @RequestBody UserSignupRequestDto signupRequestDto) {
        return ResponseEntity.ok(userService.signup(signupRequestDto));
    }
}
