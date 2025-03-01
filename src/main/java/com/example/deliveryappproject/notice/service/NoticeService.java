package com.example.deliveryappproject.notice.service;

import com.example.deliveryappproject.notice.dto.response.NoticeResponseDto;
import com.example.deliveryappproject.notice.entity.Notice;
import com.example.deliveryappproject.notice.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public Notice createNotice(Long storeId, String title, String contents) {
        Store store = storeRepository.findById(storeId).orElseThrow(
                () -> new RuntimeException("가게를 찾을수 없습니다.")
        );
        Notice notice = Notice.builder()
                .store(store)
                .title(title)
                .contents(contents)
                .build();
        return noticeRepository.save(notice);
    }

    @Transactional(readOnly = true)
    public List<NoticeResponseDto> getStoreNotices(Long storeid) {
        if (!storeRepository.existsById(storeid)) {
            throw new RuntimeException("가게를 찾을수 없습니다.");
        }
        return noticeRepository.findByStoreIdOrderByCreatedAtDesc(storeid).stream()
                .map(notice -> new NoticeResponseDto(notice.getId(), notice.getTitle(), notice.getContents()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updateNotice(Long noticeId, String title, String contents) {
        Notice notice = noticeRepository.findById(noticeId).orElseThrow(
                () -> new RuntimeException("공지를 찾을수 없습니다.")
        );
        notice.updateNotice(title, contents);
        return noticeId;
    }

    @Transactional
    public void deleteNotice(Long noticeId) {
        if (!noticeRepository.existsById(noticeId)) {
            throw new RuntimeException("삭제할 공지를 찾을수 없습니다.");
        }
        noticeRepository.deleteById(noticeId);
    }

    // 공지가 존재하는지 확인하는 기능
    @Transactional(readOnly = true)
    public boolean existsById(Long noticeId) {
        return noticeRepository.existsById(noticeId);
    }
}
