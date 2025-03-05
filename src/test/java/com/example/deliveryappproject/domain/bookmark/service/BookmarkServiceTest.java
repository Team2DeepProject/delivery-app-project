package com.example.deliveryappproject.domain.bookmark.service;

import com.example.deliveryappproject.domain.bookmark.dto.response.BookmarkResponseDto;
import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import com.example.deliveryappproject.domain.bookmark.repository.BookmarkRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.service.StoreService;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class BookmarkServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;
    @Mock
    private UserService userService;
    @Mock
    private StoreService storeService;
    @InjectMocks
    private BookmarkService bookmarkService;

    @Test
    void 즐겨찾기_추가_삭제_토글_테스트() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;
        User user = new User(userId);
        Store store = new Store(user, "일식가게", null, null, null);
        Bookmark bookmark = new Bookmark(user, store);

        given(userService.getUserById(any())).willReturn(user);
        given(storeService.findStoreByIdOrElseThrow(any())).willReturn(store);
        given(bookmarkRepository.findByUserIdAndStoreId(any(), any())).willReturn(Optional.empty());

        // When
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, userId);

        // Then
        assertThat(isBookmarked).isTrue();
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }

    @Test
    void 확인_불가능한_사용자_즐겨찾기_예외처리() {
        // Given
        Long userId = 1L;
        Long storeId = 1L;

        given(userService.getUserById(userId)).willThrow(new RuntimeException("사용자를 찾을수 없음"));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> bookmarkService.toggleUserBookmark(storeId, userId));
        assertThat(exception.getMessage()).isEqualTo("사용자를 찾을수 없음");
    }

    @Test
    void 즐겨찾기_조회() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User(userId);
        Store store1 = new Store(user, "중식가게", null, null, null);
        Store store2 = new Store(user, "양식가게", null, null, null);
        Bookmark bookmark1 = new Bookmark(user, store1);
        Bookmark bookmark2 = new Bookmark(user, store2);

        Page<Bookmark> bookmarkPage = new PageImpl<>(List.of(bookmark1, bookmark2), pageable, 2);

        given(bookmarkRepository.findByUserId(userId, pageable)).willReturn(bookmarkPage);

        // When
        Page<BookmarkResponseDto> bookmarks = bookmarkService.getUserBookmarks(userId, pageable);

        // Then
        assertThat(bookmarks).isNotNull();
        assertThat(bookmarks.getContent()).isNotNull();
        assertThat(bookmarks.getContent()).hasSize(2);
    }
}