package com.example.deliveryappproject.domain.category.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.category.entity.Category;
import com.example.deliveryappproject.domain.category.entity.CategoryStore;
import com.example.deliveryappproject.domain.category.repository.CategoryStoreRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.service.StoreService;
import com.example.deliveryappproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryStoreService {

    private final StoreService storeService;
    private final CategoryService categoryService;
    private final CategoryStoreRepository categoryStoreRepository;

    public void createCategoryStore(AuthUser authUser, Long storeId, Long categoryId) {
        Store findStore = storeService.findStoreByIdOrElseThrow(storeId);

        if (!Objects.equals(findStore.getUser().getId(), authUser.getId())) {
            throw new ForbiddenException("설정 가능한 유저가 아닙니다.");
        }

        Category findCategory = categoryService.findCategoryByIdOrElseThrow(categoryId);

        User user = new User(authUser.getId());
        CategoryStore categoryStore = new CategoryStore(user, findCategory, findStore);

        categoryStoreRepository.save(categoryStore);
    }

    public void deleteCategoryStore(AuthUser authUser, Long storeId, Long categoryId) {
        Store findStore = storeService.findStoreByIdOrElseThrow(storeId);

        if (!Objects.equals(findStore.getUser().getId(), authUser.getId())) {
            throw new ForbiddenException("설정 가능한 유저가 아닙니다.");
        }

        CategoryStore categoryStore = findByStoreAndCategoryOrElseThrow(categoryId, storeId);

        categoryStoreRepository.delete(categoryStore);
    }

    private CategoryStore findByStoreAndCategoryOrElseThrow(Long categoryId, Long storeId) {
        return categoryStoreRepository.findByStoreAndCategory(categoryId, storeId).orElseThrow(
                () -> new NotFoundException("Not Found Category-Store")
        );
    }
}
