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

    // 즐겨찾기 등록-삭제
    @PostMapping("/{storeId}")
    public ResponseEntity<String> toggleUserBookmark(@PathVariable Long storeId,
                                                     @SessionAttribute(name = "userId", required = false) Long userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, userId);
        return ResponseEntity.ok(isBookmarked ? "즐겨찾기 추가 완료" : "즐겨찾기 삭제 완료");
    }

    // 즐겨찾기 조회
    @GetMapping
    public ResponseEntity<List<BookmarkResponseDto>> getUserBookmarks(@SessionAttribute(name = "userId", required = false) Long userId
    ) {
        if (userId == null) {
            return ResponseEntity.status(401).body(null); // 401 Unauthorized 반환
        }
        return ResponseEntity.ok(bookmarkService.getUserBookmarks(userId));
    }
}
