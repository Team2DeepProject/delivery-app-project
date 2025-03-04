package com.example.deliveryappproject.web.category.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.Response;
import com.example.deliveryappproject.domain.category.dto.response.CategoryStoreResponse;
import com.example.deliveryappproject.domain.category.service.CategoryStoreService;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CategoryStoreController {

    private final CategoryStoreService categoryStoreService;

    @AuthPermission(role = UserRole.OWNER)
    @PostMapping("/stores/{storeId}/categorys/{categoryId}")
    public Response<Void> createCategoryStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @PathVariable Long categoryId
    ) {
        categoryStoreService.createCategoryStore(authUser, storeId, categoryId);
        return Response.empty();
    }

    @AuthPermission(role = UserRole.OWNER)
    @DeleteMapping("/stores/{storeId}/categorys/{categoryId}")
    public Response<Void> deleteCategoryStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @PathVariable Long categoryId
    ) {
        categoryStoreService.deleteCategoryStore(authUser, storeId, categoryId);
        return Response.empty();
    }

    @GetMapping("/categorys/{categoryId}")
    public Response<CategoryStoreResponse> getCategoryStore(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.of(categoryStoreService.getCategoryStore(categoryId, page, size));
    }
}
