package com.example.deliveryappproject.web.category.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.category.service.CategoryStoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class CategoryStoreController {

    private final CategoryStoreService categoryStoreService;

    @PostMapping("/{storeId}/categorys/{categoryId}")
    public void createCategoryStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @PathVariable Long categoryId
    ) {
        categoryStoreService.createCategoryStore(authUser, storeId, categoryId);
    }

    @DeleteMapping("/{storeId}/categorys/{categoryId}")
    public void deleteCategoryStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @PathVariable Long categoryId
    ) {
        categoryStoreService.deleteCategoryStore(authUser, storeId, categoryId);
    }
}
