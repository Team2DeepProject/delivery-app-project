package com.example.deliveryappproject.domain.review.repository;

import com.example.deliveryappproject.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByStoreId(Long storeId);  // 특정 가게의 모든 리뷰 조회
}
