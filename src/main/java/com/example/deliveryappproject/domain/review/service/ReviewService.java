package com.example.deliveryappproject.domain.review.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.review.dto.request.ReviewCreateRequest;
import com.example.deliveryappproject.domain.review.dto.response.ReviewResponse;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.review.repository.ReviewRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public void createReview(AuthUser authUser, Long storeId, ReviewCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("가게를 찾을 수 없습니다."));
        User user = new User(authUser.getId());

        Review review = Review.builder()
                .store(store)
                .user(user)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long storeId) {
        List<Review> reviews = reviewRepository.findByStoreId(storeId);
        return reviews.stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }
}
