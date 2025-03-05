package com.example.deliveryappproject.web.storenotice.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.storenotice.dto.request.NoticeRequestDto;
import com.example.deliveryappproject.domain.storenotice.dto.response.NoticeResponseDto;
import com.example.deliveryappproject.domain.storenotice.service.NoticeService;
import com.example.deliveryappproject.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 생성
    @PostMapping("/{storeId}")
    public Response<Long> createNotice(@PathVariable Long storeId,
                                       @Auth AuthUser authUser,
                                       @RequestBody NoticeRequestDto request
    ) {
        return Response.of(noticeService.createNotice(storeId, request.getTitle(), request.getContents()));
    }

    // 공지 조회
    @GetMapping("/{storeId}")
    public Response<NoticeResponseDto> getStoreNotices(@PathVariable Long storeId,
                                                       @SortDefault(sort = "createdAt", direction = DESC) Pageable pageable
    ) {
        return Response.fromPage(noticeService.getStoreNotices(storeId, pageable));
    }

    // 공지 수정
    @PutMapping("/{noticeId}")
    public Response<Long> updateNotice(@PathVariable Long noticeId,
                                       @Auth AuthUser authUser,
                                       @RequestBody NoticeRequestDto request
    ) {
        return Response.of(noticeService.updateNotice(noticeId, request.getTitle(), request.getContents()));
    }

    // 공지 삭제
    @DeleteMapping("/{noticeId}")
    public Response<Void> deleteNotice(@PathVariable Long noticeId,
                                       @Auth AuthUser authUser
    ) {
        noticeService.deleteNotice(authUser.getId(), noticeId);
        return Response.empty();
    }
}