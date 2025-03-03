package com.example.deliveryappproject.web.user.controller;


import com.example.deliveryappproject.domain.user.dto.request.UserSignupRequest;
import com.example.deliveryappproject.domain.user.dto.response.UserResponse;
import com.example.deliveryappproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@Valid @RequestBody UserSignupRequest signupRequestDto) {
        userService.signup(signupRequestDto);
        return ResponseEntity.ok("회원가입 되셨습니다.");
    }

    //회원목록 전체조회
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

}
