package com.example.deliveryappproject.domain.category.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.category.dto.request.CategoryCreateRequest;
import com.example.deliveryappproject.domain.category.dto.request.CategoryUpdateRequest;
import com.example.deliveryappproject.domain.category.entity.Category;
import com.example.deliveryappproject.domain.category.repository.CategoryRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    /* createCategoryStore */
    @Test
    void 카테고리작성_성공() {
        // given
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        CategoryCreateRequest categoryCreateRequest = new CategoryCreateRequest("한식");

        // when
        categoryService.createCategory(authUser, categoryCreateRequest);

        // then
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void 카테고리수정_이름_수정_성공() {
        // given
        Long categoryId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Category category = new Category(new User(1L), "중식");

        String newCategoryName = "중식";
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest(newCategoryName);

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when
        categoryService.updateCategory(authUser, categoryId,categoryUpdateRequest);

        // then
        assertEquals(newCategoryName, category.getName());
    }

    @Test
    void 카테고리수정_작성자가_아닐시_실패() {
        // given
        Long categoryId = 1L;
        AuthUser authUser = new AuthUser(2L, "email@email.com", UserRole.USER);
        Category category = new Category(new User(1L), "중식");

        String newCategoryName = "중식";
        CategoryUpdateRequest categoryUpdateRequest = new CategoryUpdateRequest(newCategoryName);

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> categoryService.updateCategory(authUser, categoryId, categoryUpdateRequest),
                "수정 가능한 유저가 아닙니다.");
    }

    @Test
    void 카테고리삭제_성공() {
        // given
        Long categoryId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Category category = new Category(new User(1L), "중식");

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when
        categoryService.deleteCategory(authUser, categoryId);

        // then
        verify(categoryRepository, times(1)).delete(any(Category.class));
    }

    @Test
    void 카테고리삭제_작성자가_아닐시_실패() {
        // given
        Long categoryId = 1L;
        AuthUser authUser = new AuthUser(2L, "email@email.com", UserRole.USER);
        Category category = new Category(new User(1L), "중식");

        given(categoryRepository.findById(anyLong())).willReturn(Optional.of(category));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> categoryService.deleteCategory(authUser, categoryId),
                "삭제 가능한 유저가 아닙니다.");
    }

    @Test
    void 카테고리검색_카테고리가_없을시_실패() {
        // given
        Long categoryId = 1L;

        given(categoryRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> categoryService.findCategoryByIdOrElseThrow(categoryId),
                "Not Found Category");
    }
}
