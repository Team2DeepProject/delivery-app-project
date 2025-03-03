package com.example.deliveryappproject.web.category.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.category.dto.request.CategoryCreateRequest;
import com.example.deliveryappproject.domain.category.dto.request.CategoryUpdateRequest;
import com.example.deliveryappproject.domain.category.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorys")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public void createCategory(
            @Auth AuthUser authUser,
            @Valid @RequestBody CategoryCreateRequest categoryCreateRequest
    ) {
        categoryService.createCatetory(authUser, categoryCreateRequest);
    }

    @PatchMapping("/{categoryId}")
    public void updateCategoryName(
            @Auth AuthUser authUser,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest
    ) {
        categoryService.updateCategory(authUser, categoryId, categoryUpdateRequest);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteCategory(
            @Auth AuthUser authUser,
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(authUser, categoryId);
    }
}
