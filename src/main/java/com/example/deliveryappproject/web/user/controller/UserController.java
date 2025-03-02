package com.example.deliveryappproject.web.user.controller;

import com.example.deliveryappproject.domain.user.dto.request.UserSignupRequest;
import com.example.deliveryappproject.domain.user.dto.response.UserSignupResponse;
import com.example.deliveryappproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponse> signup(@Valid @RequestBody UserSignupRequest signupRequestDto) {
        return ResponseEntity.ok(userService.signup(signupRequestDto));
    }
}
