package com.example.deliveryappproject.notice.repository;

import com.example.deliveryappproject.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    List<Notice> findByStoreIdOrderByCreatedAtDesc(Long storeId); // 공지사항을 최신순을 조회
}
