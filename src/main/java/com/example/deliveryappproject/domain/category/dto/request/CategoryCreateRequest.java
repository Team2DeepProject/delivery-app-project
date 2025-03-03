package com.example.deliveryappproject.domain.category.dto.request;

import com.example.deliveryappproject.domain.category.entity.Category;
import com.example.deliveryappproject.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryCreateRequest {
    @NotBlank(message = "카테고리 명은 필수 입력 값입니다.")
    @Size(max = 10, message = "카테고리 명은 10자 이하로 입력해주세요.")
    private String name;

    public Category toEntity(User user) {
        return new Category(
                user,
                this.name
        );
    }
}
