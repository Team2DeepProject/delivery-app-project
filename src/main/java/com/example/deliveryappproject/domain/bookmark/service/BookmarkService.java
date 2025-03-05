package com.example.deliveryappproject.domain.bookmark.service;

import com.example.deliveryappproject.domain.bookmark.dto.response.BookmarkResponseDto;
import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import com.example.deliveryappproject.domain.bookmark.repository.BookmarkRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.service.StoreService;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserService userService;
    private final StoreService storeService;

    // 토글형식 즐겨찾기 추가 삭제
    @Transactional
    public boolean toggleUserBookmark(Long storeId, Long userId) {
        User user = userService.findUserByIdOrElseThrow(userId);
        Store store = storeService.findStoreByIdOrElseThrow(storeId);

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
    public Page<BookmarkResponseDto> getUserBookmarks(Long userId, Pageable pageable) {
        return bookmarkRepository.findByUserId(userId, pageable)
                .map(BookmarkResponseDto::fromEntity);
    }
}