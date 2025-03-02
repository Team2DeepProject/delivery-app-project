package com.example.deliveryappproject.domain.bookmark.controller;

import com.example.deliveryappproject.domain.bookmark.dto.response.BookmarkResponseDto;
import com.example.deliveryappproject.domain.bookmark.service.BookmarkService;
import com.example.deliveryappproject.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 즐겨찾기 조회
    @GetMapping
    public Response<List<BookmarkResponseDto>> getUserBookmarks(@SessionAttribute(name = "userId", required = false) Long userId) {
        if (userId == null) {
            return Response.error("로그인이 필요합니다.");
        }
        return Response.of(bookmarkService.getUserBookmarks(userId), "즐겨찾기 조회 성공");
    }

    // 즐겨찾기 등록-삭제
    @PostMapping("/{storeId}")
    public Response<String> toggleUserBookmark(@PathVariable Long storeId,
                                               @SessionAttribute(name = "userId", required = false) Long userId
    ) {
        if (userId == null) {
            return Response.error("로그인이 필요합니다.");
        }
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, userId);
        return Response.of(isBookmarked ? "즐겨찾기 추가 완료" : "즐겨찾기 삭제 완료","요청 성공");
    }
}
