package com.example.deliveryappproject.domain.bookmark.service;

import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import com.example.deliveryappproject.domain.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkCountService {

    private final BookmarkRepository bookmarkRepository;

    // 가게의 북마크 조회 (store에서 사용)
    public List<Bookmark> findByStoreId(Long storeId) {
        return bookmarkRepository.findByStoreId(storeId);
    }
}
