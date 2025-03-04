package com.example.deliveryappproject.domain.notice.repository;

import com.example.deliveryappproject.domain.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findByStoreIdOrderByCreatedAtDesc(Long storeId, Pageable pageable); // 공지사항을 최신순을 조회
}
