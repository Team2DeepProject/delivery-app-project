package com.example.deliveryappproject.domain.category.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import com.example.deliveryappproject.domain.bookmark.service.BookmarkCountService;
import com.example.deliveryappproject.domain.bookmark.service.BookmarkService;
import com.example.deliveryappproject.domain.category.dto.response.CategoryStoreResponse;
import com.example.deliveryappproject.domain.category.entity.Category;
import com.example.deliveryappproject.domain.category.entity.CategoryStore;
import com.example.deliveryappproject.domain.category.repository.CategoryStoreRepository;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.entity.StoreState;
import com.example.deliveryappproject.domain.store.service.StoreService;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryStoreServiceTest {

    @Mock
    private StoreService storeService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryStoreRepository categoryStoreRepository;

    @Mock
    private BookmarkCountService bookmarkCountService;

    @InjectMocks
    private CategoryStoreService categoryStoreService;

    /* createCategoryStore */
    @Test
    void 카테고리등록_성공() {
        // given
        Long categoryId = 1L;
        Long storeId = 100L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(storeId, new User(1L));
        Category category = new Category(categoryId);

        given(storeService.findStoreByIdOrElseThrow(anyLong())).willReturn(store);
        given(categoryService.findCategoryByIdOrElseThrow(anyLong())).willReturn(category);

        // when
        categoryStoreService.createCategoryStore(authUser, storeId, categoryId);

        // then
        verify(categoryStoreRepository, times(1)).save(any(CategoryStore.class));
    }

    @Test
    void 카테고리등록_가게를_등록한_주인이_아니면_실패() {
        // given
        Long categoryId = 1L;
        Long storeId = 100L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(storeId, new User(2L));

        given(storeService.findStoreByIdOrElseThrow(anyLong())).willReturn(store);

        // when & then
        assertThrows(ForbiddenException.class,
                () -> categoryStoreService.createCategoryStore(authUser, storeId, categoryId),
                "설정 가능한 유저가 아닙니다.");
    }

    /* deleteCategoryStore */
    @Test
    void 카테고리해제_성공() {
        // given
        Long categoryId = 1L;
        Long storeId = 100L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(storeId, new User(1L));
        CategoryStore categoryStore = new CategoryStore();

        given(storeService.findStoreByIdOrElseThrow(anyLong())).willReturn(store);
        given(categoryStoreRepository.findByStoreAndCategory(categoryId, storeId)).willReturn(Optional.of(categoryStore));

        // when
        categoryStoreService.deleteCategoryStore(authUser, storeId, categoryId);

        // then
        verify(categoryStoreRepository, times(1)).delete(any(CategoryStore.class));
    }

    @Test
    void 카테고리해제_가게를_등록한_주인이_아니면_실패() {
        // given
        Long categoryId = 1L;
        Long storeId = 100L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(storeId, new User(2L));

        given(storeService.findStoreByIdOrElseThrow(anyLong())).willReturn(store);

        // when & then
        assertThrows(ForbiddenException.class,
                () -> categoryStoreService.deleteCategoryStore(authUser, storeId, categoryId),
                "설정 가능한 유저가 아닙니다.");
    }

    @Test
    void 카테고리해제_카테고리를_등록한_데이터가_없어_실패() {
        // given
        Long categoryId = 1L;
        Long storeId = 100L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(storeId, new User(1L));

        given(storeService.findStoreByIdOrElseThrow(anyLong())).willReturn(store);
        given(categoryStoreRepository.findByStoreAndCategory(categoryId, storeId)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> categoryStoreService.deleteCategoryStore(authUser, storeId, categoryId),
                "Not Found Category-Store");
    }

    @Test
    void 카테고리가게조회_카테고리에_해당하는_가게를_조회_성공() {
        // given
        Long categoryId = 1L;
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);

        String categoryName = "한식";

        Store store1 = new Store(new User(1L), "store1", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));
        Store store2 = new Store(new User(2L), "store2", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        Category category = new Category(categoryId, new User(1L), categoryName);
        CategoryStore categoryStore1 = new CategoryStore(store1.getUser(), category, store1);
        CategoryStore categoryStore2 = new CategoryStore(store2.getUser(), category, store2);

        List<CategoryStore> categoryStores = List.of(categoryStore1, categoryStore2);
        Page<CategoryStore> categoryStorePage = new PageImpl<>(categoryStores);

        given(categoryStoreRepository.findByCategoryId(categoryId, pageable)).willReturn(categoryStorePage);
        given(bookmarkCountService.findByStoreId(store1.getId())).willReturn(Collections.nCopies(10, new Bookmark(new User(1L), store1)));
        given(bookmarkCountService.findByStoreId(store2.getId())).willReturn(Collections.nCopies(5, new Bookmark(new User(1L), store2)));

        // when
        CategoryStoreResponse categoryStore = categoryStoreService.getCategoryStore(categoryId, page, size);

        // then
        assertEquals(2, categoryStore.getCategoryStores().size());
        assertEquals(store1.getStoreName(), categoryStore.getCategoryStores().get(0).getStoreName());
        assertEquals(store2.getStoreName(), categoryStore.getCategoryStores().get(1).getStoreName());

        verify(categoryStoreRepository, times(1)).findByCategoryId(categoryId, pageable);
    }

    @Test
    void 카테고리가게조회_빈결과_조회_성공() {
        // given
        Long categoryId = 1L;
        int page = 1;
        int size = 10;

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<CategoryStore> emptyPage = Page.empty();
        given(categoryStoreRepository.findByCategoryId(categoryId, pageable)).willReturn(emptyPage);

        // when
        CategoryStoreResponse categoryStore = categoryStoreService.getCategoryStore(categoryId, page, size);

        // then
        assertEquals("", categoryStore.getCategoryName());
        assertTrue(categoryStore.getCategoryStores().isEmpty());

        verify(categoryStoreRepository, times(1)).findByCategoryId(categoryId, pageable);
    }

}
