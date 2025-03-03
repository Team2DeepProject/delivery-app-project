package com.example.deliveryappproject.domain.bookmark.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
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
    public Response<List<BookmarkResponseDto>> getUserBookmarks(@Auth AuthUser authUser) {
        return Response.of(bookmarkService.getUserBookmarks(authUser.getId()), "즐겨찾기 조회 성공");
    }

    // 즐겨찾기 등록-삭제
    @PostMapping("/{storeId}")
    public Response<String> toggleUserBookmark(@PathVariable Long storeId,
                                               @Auth AuthUser authUser
    ) {
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, authUser.getId());
        return Response.of(isBookmarked ? "즐겨찾기 추가 완료" : "즐겨찾기 삭제 완료","요청 성공");
    }
}
