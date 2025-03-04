package com.example.deliveryappproject.domain.bookmark.service;

import com.example.deliveryappproject.domain.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookmarkCountService {

    private final BookmarkRepository bookmarkRepository;

    // 가게의 북마크 조회 (store에서 사용)
    public int getCountByStoreId(Long storeId) {
        return bookmarkRepository.countByStoreId(storeId);
    }
}
