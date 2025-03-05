package com.example.deliveryappproject.web.category.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.Response;
import com.example.deliveryappproject.domain.category.dto.request.CategoryCreateRequest;
import com.example.deliveryappproject.domain.category.dto.request.CategoryUpdateRequest;
import com.example.deliveryappproject.domain.category.service.CategoryService;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categorys")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @AuthPermission(role = UserRole.ADMIN)
    @PostMapping
    public Response<Void> createCategory(
            @Auth AuthUser authUser,
            @Valid @RequestBody CategoryCreateRequest categoryCreateRequest
    ) {
        categoryService.createCategory(authUser, categoryCreateRequest);
        return Response.empty();
    }

    @AuthPermission(role = UserRole.ADMIN)
    @PatchMapping("/{categoryId}")
    public Response<Void> updateCategoryName(
            @Auth AuthUser authUser,
            @PathVariable Long categoryId,
            @Valid @RequestBody CategoryUpdateRequest categoryUpdateRequest
    ) {
        categoryService.updateCategory(authUser, categoryId, categoryUpdateRequest);
        return Response.empty();
    }

    @AuthPermission(role = UserRole.ADMIN)
    @DeleteMapping("/{categoryId}")
    public Response<Void> deleteCategory(
            @Auth AuthUser authUser,
            @PathVariable Long categoryId
    ) {
        categoryService.deleteCategory(authUser, categoryId);
        return Response.empty();
    }
}
