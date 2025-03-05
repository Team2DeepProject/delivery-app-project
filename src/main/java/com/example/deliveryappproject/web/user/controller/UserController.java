package com.example.deliveryappproject.web.user.controller;


import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.MessageResponse;
import com.example.deliveryappproject.domain.user.dto.request.UserDeleteRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserSignupRequest;
import com.example.deliveryappproject.domain.user.dto.request.UserUpdateRequest;
import com.example.deliveryappproject.domain.user.dto.response.UserResponse;
import com.example.deliveryappproject.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/signup")
    public MessageResponse signup(@Valid @RequestBody UserSignupRequest signupRequestDto) {
        userService.signup(signupRequestDto);
        return MessageResponse.of("회원가입을 축하드립니다.");
    }

    //회원목록 전체조회
    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        return ResponseEntity.ok(userService.findAll(page, size));
    }

    //로그인 회원 정보 조회
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> fetchProfile(@Auth AuthUser authUser){
        return ResponseEntity.ok(userService.fetchProfile(authUser.getId()));
    }

    //회원 정보 수정
    @PatchMapping
    public MessageResponse updateUserName(@Auth AuthUser authUser,
                                                             @RequestBody UserUpdateRequest dto){
        userService.updateUserName(authUser.getId(),dto);
        return MessageResponse.of("회원정보가 업데이트 되었습니다.");
    }

    //회원 탈퇴
    @PostMapping("/delete")
    public MessageResponse deleteAccount(@Auth AuthUser authUser,
                                                      @RequestBody UserDeleteRequest dto){
        userService.deleteUser(authUser.getId(), dto);
        return MessageResponse.of("회원탈퇴가 되었습니다.");
    }

}
