package com.example.deliveryappproject.web.category.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.category.service.CategoryStoreService;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CategoryStoreController {

    private final CategoryStoreService categoryStoreService;

    @PostMapping("/stores/{storeId}/categorys/{categoryId}")
    public void createCategoryStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @PathVariable Long categoryId
    ) {
        categoryStoreService.createCategoryStore(authUser, storeId, categoryId);
    }

    @DeleteMapping("/stores/{storeId}/categorys/{categoryId}")
    public void deleteCategoryStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @PathVariable Long categoryId
    ) {
        categoryStoreService.deleteCategoryStore(authUser, storeId, categoryId);
    }

    @GetMapping("categorys/{categoryId}")
    public Page<StoreGetAllResponse> getCategoryStore(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return categoryStoreService.getCategoryStore(categoryId, page, size);
    }
}
