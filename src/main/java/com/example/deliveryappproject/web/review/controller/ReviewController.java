package com.example.deliveryappproject.web.review.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.review.dto.request.ReviewCreateRequest;
import com.example.deliveryappproject.domain.review.dto.request.ReviewUpdateRequest;
import com.example.deliveryappproject.domain.review.dto.response.ReviewResponse;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.review.service.ReviewService;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores/{storeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @AuthPermission(role = UserRole.USER)
    @PostMapping
    public ResponseEntity<Void> createReview(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        reviewService.createReview(authUser, storeId, request);
        return ResponseEntity.ok().build();  // 성공적으로 저장된 후 200 OK 상태 코드 반환
    }

    @GetMapping
    public List<ReviewResponse> getReviews(@PathVariable Long storeId) {
        return reviewService.getReviews(storeId);
    }

    @AuthPermission(role = UserRole.USER)
    @PutMapping("/{reviewId}")
    public ReviewResponse updateReview(
            @Auth AuthUser authUser,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewUpdateRequest request
    ) {

        Review updatedReview = reviewService.updateReview(authUser, reviewId, request);

        return new ReviewResponse(updatedReview);
    }

    @AuthPermission(role = UserRole.USER)
    @DeleteMapping("/{reviewId}")
    public void deleteReview(
            @Auth AuthUser authUser,
            @PathVariable Long reviewId
    ) {
        reviewService.deleteReview(authUser, reviewId);
    }
}
