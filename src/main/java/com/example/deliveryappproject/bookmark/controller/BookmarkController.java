package com.example.deliveryappproject.bookmark.controller;

import com.example.deliveryappproject.bookmark.dto.response.BookmarkResponseDto;
import com.example.deliveryappproject.bookmark.service.BookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{storeId}")
    public ResponseEntity<Void> toggleUserBookmark(@PathVariable Long storeId,
                                                   @SessionAttribute(name = "userId") Long userId
    ) {
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, userId);

        if (isBookmarked) {
            return ResponseEntity.status(201).build(); // 즐겨찾기 시 201 반환
        } else {
            return ResponseEntity.noContent().build(); // 즐겨찾기 취소 시 204 반환
        }
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponseDto>> getUserBookmarks(@SessionAttribute(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(userId));
    }
}
