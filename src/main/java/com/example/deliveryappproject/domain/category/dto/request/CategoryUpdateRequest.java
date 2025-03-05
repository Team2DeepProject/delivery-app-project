package com.example.deliveryappproject.domain.category.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryUpdateRequest {

    @Size(max = 10, message = "카테고리 명은 10자 이하로 입력해주세요.")
    private String name;

}
