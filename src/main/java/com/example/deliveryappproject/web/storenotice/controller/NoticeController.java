package com.example.deliveryappproject.domain.notice.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.MessageResponse;
import com.example.deliveryappproject.domain.notice.dto.request.NoticeRequestDto;
import com.example.deliveryappproject.domain.notice.dto.response.NoticeResponseDto;
import com.example.deliveryappproject.domain.notice.service.NoticeService;
import com.example.deliveryappproject.common.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public MessageResponse createNotice(@PathVariable Long storeId,
                                       @Auth AuthUser authUser,
                                       @RequestBody NoticeRequestDto request
    ) {
        noticeService.createNotice(storeId, request.getTitle(), request.getContents());
        return MessageResponse.of("공지 생성 완료");
    }

    // 공지 조회
    @GetMapping("/{storeId}")
    public Response<NoticeResponseDto> getStoreNotices(@PathVariable Long storeId,
                                                       @SortDefault(sort = "createdAt", direction = DESC) Pageable pageable
    ) {
//        return Response.of(noticeService.getStoreNotices(storeId, pageable), "공지 조회 완료");
        Page<NoticeResponseDto> storeNotices = noticeService.getStoreNotices(storeId, pageable);
        return Response.fromPage(storeNotices);
    }

    // 공지 수정
    @PutMapping("/{noticeId}")
    public MessageResponse updateNotice(@PathVariable Long noticeId,
                                        @Auth AuthUser authUser,
                                        @RequestBody NoticeRequestDto request
    ) {
        noticeService.updateNotice(noticeId, request.getTitle(), request.getContents());
        return MessageResponse.of("공지 수정 완료");
    }

    // 공지 삭제
    @DeleteMapping("/{noticeId}")
    public MessageResponse deleteNotice(@PathVariable Long noticeId,
                                       @Auth AuthUser authUser
    ) {
        noticeService.deleteNotice(authUser.getId(), noticeId);
        return MessageResponse.of("공지 삭제 완료");
    }
}
