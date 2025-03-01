package com.example.deliveryappproject.notice.controller;

import com.example.deliveryappproject.notice.dto.request.NoticeRequestDto;
import com.example.deliveryappproject.notice.dto.response.NoticeResponseDto;
import com.example.deliveryappproject.notice.entity.Notice;
import com.example.deliveryappproject.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notices")
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/{storeId}")
    public ResponseEntity<Notice> createNotice(@PathVariable Long storeId,
                                               @SessionAttribute(name = "userId") Long userId,
                                               @RequestBody NoticeRequestDto request
    ) {
        Notice savedNotice = noticeService.createNotice(storeId, request.getTitle(), request.getContents());
        return ResponseEntity.status(201).body(savedNotice); // 201 Created 반환
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<List<NoticeResponseDto>> getStoreNotices(@PathVariable Long storeId) {
        return ResponseEntity.ok(noticeService.getStoreNotices(storeId));
    }

    @PutMapping("/{noticeId}")
    public ResponseEntity<Long> updateNotice(@PathVariable Long noticeId,
                                             @SessionAttribute(name = "userId") Long userId,
                                             @RequestBody NoticeRequestDto request
    ) {
        Long updatedId = noticeService.updateNotice(noticeId, request.getTitle(), request.getContents());
        return ResponseEntity.ok(updatedId);
    }

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long noticeId,
                                             @SessionAttribute(name = "userId") Long userId
    ) {
        if (!noticeService.existsById(noticeId)) {
            return ResponseEntity.notFound().build(); // 공지가 없을경우 404 Not Found 반환
        }
        noticeService.deleteNotice(noticeId);
        return ResponseEntity.noContent().build();
    }
}
