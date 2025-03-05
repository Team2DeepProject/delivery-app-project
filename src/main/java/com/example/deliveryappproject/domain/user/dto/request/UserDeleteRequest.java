package com.example.deliveryappproject.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UserDeleteRequest {

    @NotBlank
    private String password;

}
