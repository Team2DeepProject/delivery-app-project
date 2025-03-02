package com.example.deliveryappproject.domain.notice.service;

import com.example.deliveryappproject.domain.notice.dto.response.NoticeResponseDto;
import com.example.deliveryappproject.domain.notice.entity.Notice;
import com.example.deliveryappproject.domain.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final StoreRepository storeRepository;

    private Store getStoreById(Long storeId) {
        return storeRepository.findById(storeId).orElseThrow(
                () -> new RuntimeException("가게 확인 불가: " + storeId)
        );
    }

    // 공지 생성
    @Transactional
    public Long createNotice(Long storeId, String title, String contents) {
        Store store = getStoreById(storeId);
        Notice notice = noticeRepository.save(Notice.builder()
                .store(store)
                .title(title)
                .contents(contents)
                .build()
        );
        return notice.getId();
    }

    // 공지 조회
    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getStoreNotices(Long storeId, Pageable pageable) {
        getStoreById(storeId);
        return noticeRepository.findByStoreIdOrderByCreatedAtDesc(storeId, pageable)
                .map(notice -> new NoticeResponseDto(notice.getId(), notice.getTitle(), notice.getContents()));
    }

    // 공지 수정
    @Transactional
    public Long updateNotice(Long noticeId, String title, String contents) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new RuntimeException("공지 확인 불가: " + noticeId)
        );
        notice.updateNotice(title, contents);
        return notice.getId();
    }

    // 공지 삭제
    @Transactional
    public void deleteNotice(Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new RuntimeException("공지 확인 불가: " + noticeId)
        );
        noticeRepository.delete(notice);
    }
}
