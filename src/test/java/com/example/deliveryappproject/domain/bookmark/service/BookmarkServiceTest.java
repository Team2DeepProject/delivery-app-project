package com.example.deliveryappproject.domain.bookmark.service;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

        given(userService.getUserById(userId)).willReturn(user);
        given(storeService.findStoreByIdOrElseThrow(storeId)).willReturn(store);
        given(bookmarkRepository.findByUserIdAndStoreId(userId, storeId)).willReturn(Optional.empty());

        // When
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, userId);

        // Then
        assertThat(isBookmarked).isTrue();
        verify(bookmarkRepository, times(1)).save(any(Bookmark.class));
    }
}