package com.example.deliveryappproject.bookmark.service;

import com.example.deliveryappproject.bookmark.dto.response.BookmarkResponseDto;
import com.example.deliveryappproject.bookmark.entity.Bookmark;
import com.example.deliveryappproject.bookmark.repository.BookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public boolean toggleUserBookmark(Long storeId, Long userId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("해당 가게를 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("해당 사용자를 찾을 수 없습니다."));

        boolean exists = bookmarkRepository.existsByUserIdAndStoreId(userId, storeId);

        if (exists) {
            bookmarkRepository.deleteByUserIdAndStoreId(userId, storeId);
            return false;
        } else {
            Bookmark bookmark = Bookmark.builder()
                    .user(user)
                    .store(store)
                    .build();
            bookmarkRepository.save(bookmark);
            return true;
        }
    }

    @Transactional(readOnly = true)
    public List<BookmarkResponseDto> getUserBookmarks(Long userId) {
        return bookmarkRepository.findByUserId(userId).stream()
                .map(bookmark -> new BookmarkResponseDto(
                        bookmark.getStore().getId(),
                        bookmark.getStore().getName()
                ))
                .collect(Collectors.toList());
    }
}