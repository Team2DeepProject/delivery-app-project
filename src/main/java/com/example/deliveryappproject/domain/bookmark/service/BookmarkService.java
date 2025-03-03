package com.example.deliveryappproject.domain.bookmark.service;

import com.example.deliveryappproject.domain.bookmark.dto.response.BookmarkResponseDto;
import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import com.example.deliveryappproject.domain.bookmark.repository.BookmarkRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final StoreRepository storeRepository;

    // 토글형식 즐겨찾기 추가 삭제
    @Transactional
    public boolean toggleUserBookmark(Long storeId, Long userId) {
        User user = userService.getUserById(userId);
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new RuntimeException("가게를 찾을수 없습니다.")
        );

        return bookmarkRepository.findByUserIdAndStoreId(userId, storeId)
                .map(bookmark -> {
                    bookmarkRepository.delete(bookmark);
                    return false;
                })
                .orElseGet(() -> {
                    bookmarkRepository.save(Bookmark.builder()
                            .user(user)
                            .store(store)
                            .build());
                    return true;
                });

    }

    // 즐겨찾기 조회
    @Transactional(readOnly = true)
    public List<BookmarkResponseDto> getUserBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(BookmarkResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}