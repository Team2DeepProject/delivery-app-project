package com.example.deliveryappproject.domain.bookmark.service;

import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import com.example.deliveryappproject.domain.bookmark.repository.BookmarkRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookmarkCountServiceTest {

    @Mock
    private BookmarkRepository bookmarkRepository;
    @InjectMocks
    private BookmarkCountService bookmarkCountService;

    /* getCountByStoreId */
    @Test
    void 즐겨찾기검색_성공() {
        Long storeId = 1L;
        Bookmark bookmark1 = new Bookmark(new User(2L),
                new Store(new User(1L), "store1", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000)));
        Bookmark bookmark2 = new Bookmark(new User(3L),
                new Store(new User(1L), "store1", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000)));

        List<Bookmark> mockBookmarks = List.of(bookmark1, bookmark2);
        given(bookmarkRepository.findByStoreId(storeId)).willReturn(mockBookmarks);

        // when
        List<Bookmark> result = bookmarkCountService.findByStoreId(storeId);

        // then
        assertEquals(2, result.size()); // 북마크 개수가 2개인지 확인
        assertEquals(bookmark1.getId(), result.get(0).getId());
        assertEquals(bookmark2.getId(), result.get(1).getId());

        verify(bookmarkRepository, times(1)).findByStoreId(storeId);
    }
}
