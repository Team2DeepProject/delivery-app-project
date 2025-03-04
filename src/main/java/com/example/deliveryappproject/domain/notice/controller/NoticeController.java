package com.example.deliveryappproject.domain.notice.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.notice.dto.request.NoticeRequestDto;
import com.example.deliveryappproject.domain.notice.dto.response.NoticeResponseDto;
import com.example.deliveryappproject.domain.notice.service.NoticeService;
import com.example.deliveryappproject.common.response.Response;
import com.example.deliveryappproject.domain.user.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.*;

import static org.springframework.data.domain.Sort.Direction.DESC;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stores/{storeId}/notices")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 생성
    @AuthPermission(role = UserRole.OWNER)
    @PostMapping
    public Response<Long> createNotice(@PathVariable Long storeId,
                                       @Auth AuthUser authUser,
                                       @RequestBody NoticeRequestDto request
    ) {
        return Response.of(noticeService.createNotice(storeId, request.getTitle(), request.getContents()), "공지 생성 완료");
    }

    // 공지 조회
    @GetMapping
    public Response<Page<NoticeResponseDto>> getStoreNotices(@PathVariable Long storeId,
                                                             @SortDefault(sort = "createdAt", direction = DESC) Pageable pageable
    ) {
        return Response.of(noticeService.getStoreNotices(storeId, pageable), "공지 조회 완료");
    }

    // 공지 수정
    @AuthPermission(role = UserRole.OWNER)
    @PutMapping("/{noticeId}")
    public Response<Long> updateNotice(@PathVariable Long noticeId,
                                       @Auth AuthUser authUser,
                                       @RequestBody NoticeRequestDto request
    ) {
        return Response.of(noticeService.updateNotice(noticeId, request.getTitle(), request.getContents()), "공지 수정 완료");
    }

    // 공지 삭제
    @AuthPermission(role = UserRole.OWNER)
    @DeleteMapping("/{noticeId}")
    public Response<Void> deleteNotice(@PathVariable Long noticeId,
                                       @Auth AuthUser authUser
    ) {
        noticeService.deleteNotice(authUser.getId(), noticeId);
        return Response.empty("공지 삭제 완료");
    }
}
