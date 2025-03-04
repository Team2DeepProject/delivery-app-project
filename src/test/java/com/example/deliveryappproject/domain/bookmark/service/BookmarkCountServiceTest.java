package com.example.deliveryappproject.domain.bookmark.service;

import com.example.deliveryappproject.domain.bookmark.repository.BookmarkRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        // given
        Long storeId = 1L;
        int expectedCount = 5;

        given(bookmarkRepository.countByStoreId(storeId)).willReturn(expectedCount);

        // when
        int result = bookmarkCountService.getCountByStoreId(storeId);

        // Then
        assertEquals(expectedCount, result);

        verify(bookmarkRepository, times(1)).countByStoreId(storeId);
    }
}
