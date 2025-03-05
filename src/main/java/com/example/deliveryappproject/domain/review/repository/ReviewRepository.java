package com.example.deliveryappproject.domain.review.repository;

import com.example.deliveryappproject.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 가게와 사용자가 이미 리뷰를 작성한 적이 있는지 확인
    boolean existsByStoreIdAndUserId(Long storeId, Long userId);

    List<Review> findByStoreId(Long storeId);  // 특정 가게의 모든 리뷰 조회
}
