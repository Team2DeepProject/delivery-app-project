package com.example.deliveryappproject.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
public class UserSignupRequest {

    @NotBlank @Email(message="이메일 형식을 지켜주세요.")
    private String email;

    @NotBlank(message="패스워드는 필수 항목입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,}",
            message = "비밀번호는 8자 이상이며, 영어, 숫자, 특수문자를 최소 1글자 이상 포함해야 합니다.")
    private String password;

    @NotBlank(message="패스워드 확인은 필수 항목입니다.")
    private String passwordCheck;       // 패스워드 확인 추가

    @NotBlank
    private String userName;

    @NotBlank
    private String userRole;

}
