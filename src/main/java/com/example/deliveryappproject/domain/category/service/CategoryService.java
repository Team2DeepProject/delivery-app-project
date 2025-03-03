package com.example.deliveryappproject.domain.category.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.category.dto.request.CategoryCreateRequest;
import com.example.deliveryappproject.domain.category.dto.request.CategoryUpdateRequest;
import com.example.deliveryappproject.domain.category.entity.Category;
import com.example.deliveryappproject.domain.category.repository.CategoryRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCategory(AuthUser authUser, CategoryCreateRequest categoryCreateRequest) {

        User user = new User(authUser.getId());
        Category category = categoryCreateRequest.toEntity(user);

        categoryRepository.save(category);
    }

    @Transactional
    public void updateCategory(AuthUser authUser, Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {

        Category findCategory = findCategoryByIdOrElseThrow(categoryId);

        if (!Objects.equals(findCategory.getUser().getId(), authUser.getId())) {
            throw new ForbiddenException("수정 가능한 유저가 아닙니다.");
        }

        findCategory.updateCategoryName(categoryUpdateRequest.getName());
    }

    public void deleteCategory(AuthUser authUser, Long categoryId) {
        Category findCategory = findCategoryByIdOrElseThrow(categoryId);

        if (!Objects.equals(findCategory.getUser().getId(), authUser.getId())) {
            throw new ForbiddenException("삭제 가능한 유저가 아닙니다.");
        }
        categoryRepository.delete(findCategory);
    }

    public Category findCategoryByIdOrElseThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Not Found Category")
        );
    }
}
