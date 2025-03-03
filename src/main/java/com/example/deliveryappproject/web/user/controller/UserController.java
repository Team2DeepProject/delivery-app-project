package com.example.deliveryappproject.web.user.controller;


import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.user.dto.request.UserDeleteRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserSignupRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserUpdateRequest;
import com.example.deliveryappproject.domain.user.dto.response.UserResponse;
import com.example.deliveryappproject.domain.user.dto.response.UserUpdateResponse;
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
        return ResponseEntity.ok("회원가입을 축하드립니다.");
    }

    //회원목록 전체조회
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    //로그인 회원 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> fetchProfile(@Auth AuthUser authUser){
        return ResponseEntity.ok(userService.fetchProfile(authUser.getId()));
    }

    //회원 정보 수정
    @PatchMapping
    public ResponseEntity<UserUpdateResponse> updateUserName(@Auth AuthUser authUser,
                                                             @RequestBody UserUpdateRequest dto){
        return ResponseEntity.ok(userService.updateUserName(authUser.getId(), dto));
    }

    //회원 탈퇴
    @PostMapping("/delete")
    public ResponseEntity<String> deleteAccount(@Auth AuthUser authUser,
                                                      @RequestBody UserDeleteRequest dto){
        userService.deleteUser(authUser.getId(), dto);
        return ResponseEntity.ok("회원탈퇴가 되었습니다.");
    }

}
