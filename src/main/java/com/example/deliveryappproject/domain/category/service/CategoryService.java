package com.example.deliveryappproject.domain.category.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.category.dto.request.CategoryCreateRequest;
import com.example.deliveryappproject.domain.category.entity.Category;
import com.example.deliveryappproject.domain.category.repository.CategoryRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public void createCatetory(AuthUser authUser, CategoryCreateRequest categoryCreateRequest) {
        User user = new User(authUser.getId());

        Category category = categoryCreateRequest.toEntity(user);

        categoryRepository.save(category);
    }
}
