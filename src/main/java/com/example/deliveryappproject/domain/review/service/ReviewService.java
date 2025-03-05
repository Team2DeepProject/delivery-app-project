package com.example.deliveryappproject.domain.review.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.review.dto.request.ReviewCreateRequest;
import com.example.deliveryappproject.domain.review.dto.request.ReviewUpdateRequest;
import com.example.deliveryappproject.domain.review.dto.response.ReviewResponse;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.review.repository.ReviewRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    @Transactional
    public ResponseEntity<Void> createReview(AuthUser authUser, Long storeId, ReviewCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new NotFoundException("가게를 찾을 수 없습니다."));

        User user = userRepository.findById(authUser.getId())
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        boolean existingReview = reviewRepository.existsByStoreIdAndUserId(storeId, user.getId());
        if (existingReview) {
            throw new BadRequestException("한 가게에 하나의 리뷰만 작성할 수 있습니다.");
        }

        Review review = Review.builder()
                .store(store)
                .user(user)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        reviewRepository.save(review);

        return ResponseEntity.ok().build();  // 성공적으로 저장된 후 200 OK 상태 코드 반환
    }


    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviews(Long storeId) {
        List<Review> reviews = reviewRepository.findByStoreId(storeId);
        return reviews.stream()
                .map(ReviewResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Review updateReview(AuthUser authUser, Long reviewId, ReviewUpdateRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(authUser.getId())) {
            throw new ForbiddenException("본인의 리뷰만 수정할 수 있습니다.");
        }

        review.updateReview(request.getContent(), request.getRating());

        return review;
    }


    @Transactional
    public void deleteReview(AuthUser authUser, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new NotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(authUser.getId())) {
            throw new ForbiddenException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }
}
