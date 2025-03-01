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
    public ResponseEntity<String> toggleUserBookmark(@PathVariable Long storeId,
                                                   @SessionAttribute(name = "userId") Long userId
    ) {
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, userId);
        return ResponseEntity.ok(isBookmarked ? "즐겨찾기 추가 완료" : "즐겨찾기 삭제 완료");
    }

    @GetMapping
    public ResponseEntity<List<BookmarkResponseDto>> getUserBookmarks(@SessionAttribute(name = "userId") Long userId
    ) {
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(userId));
    }
}
