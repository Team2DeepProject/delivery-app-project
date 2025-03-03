package com.example.deliveryappproject.user.controller;

import com.example.deliveryappproject.user.dto.UserResponseDto;
import com.example.deliveryappproject.user.dto.UserSignupRequestDto;
import com.example.deliveryappproject.user.dto.UserSignupResponseDto;
import com.example.deliveryappproject.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserSignupResponseDto> signup(@Valid @RequestBody UserSignupRequestDto signupRequestDto) {
        return ResponseEntity.ok(userService.signup(signupRequestDto));
    }

    //회원목록 전체조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

}
