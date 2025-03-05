package com.example.deliveryappproject.domain.bookmark.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.MessageResponse;
import com.example.deliveryappproject.domain.bookmark.dto.response.BookmarkResponseDto;
import com.example.deliveryappproject.domain.bookmark.service.BookmarkService;
import com.example.deliveryappproject.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 즐겨찾기 조회
    @GetMapping
    public Response<BookmarkResponseDto> getUserBookmarks(@Auth AuthUser authUser, Pageable pageable) {
        Page<BookmarkResponseDto> bookmarks = bookmarkService.getUserBookmarks(authUser.getId(), pageable);
        return Response.fromPage(bookmarks);
//        return Response.of(, "즐겨찾기 조회 성공");
    }

    // 즐겨찾기 등록-삭제
    @PostMapping("/{storeId}")
    public MessageResponse toggleUserBookmark(@PathVariable Long storeId,
                                              @Auth AuthUser authUser
    ) {
        boolean isBookmarked = bookmarkService.toggleUserBookmark(storeId, authUser.getId());
        return MessageResponse.of(isBookmarked ? "즐겨찾기 추가 완료" : "즐겨찾기 삭제 완료");
    }
}
