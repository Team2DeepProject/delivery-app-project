package com.example.deliveryappproject.web.category.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.category.dto.request.CategoryCreateRequest;
import com.example.deliveryappproject.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/categorys")
    public void createCategory(
            @Auth AuthUser authUser,
            @Valid @RequestBody CategoryCreateRequest categoryCreateRequest
    ) {
        categoryService.createCatetory(authUser, categoryCreateRequest);
    }
}
