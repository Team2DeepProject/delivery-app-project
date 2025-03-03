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
import jakarta.validation.Valid;
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
    public void createCatetory(AuthUser authUser, CategoryCreateRequest categoryCreateRequest) {

        existByNameOrElseThrow(categoryCreateRequest.getName());

        User user = new User(authUser.getId());
        Category category = categoryCreateRequest.toEntity(user);

        try {
            categoryRepository.save(category);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("이미 존재하는 데이터입니다.");
        }
    }

    @Transactional
    public void updateCategory(AuthUser authUser, Long categoryId, CategoryUpdateRequest categoryUpdateRequest) {

        existByNameOrElseThrow(categoryUpdateRequest.getName());

        Category findCategory = findCategoryByIdOrElseThrow(categoryId);

        if (!Objects.equals(findCategory.getUser().getId(), authUser.getId())) {
            throw new ForbiddenException("수정 가능한 유저가 아닙니다.");
        }

        try {
            findCategory.updateCategoryName(categoryUpdateRequest.getName());
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("이미 존재하는 데이터입니다.");
        }
    }

    private Category findCategoryByIdOrElseThrow(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException("Not Found Category")
        );
    }

    private void existByNameOrElseThrow(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new BadRequestException("이미 존재하는 카테고리입니다.");
        }
    }
}
