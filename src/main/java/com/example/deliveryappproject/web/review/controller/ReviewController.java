package com.example.deliveryappproject.web.review.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.review.dto.request.ReviewCreateRequest;
import com.example.deliveryappproject.domain.review.dto.response.ReviewResponse;
import com.example.deliveryappproject.domain.review.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores/{storeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public void createReview(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @Valid @RequestBody ReviewCreateRequest request
    ) {
        reviewService.createReview(authUser, storeId, request);
    }

    @GetMapping
    public List<ReviewResponse> getReviews(@PathVariable Long storeId) {
        return reviewService.getReviews(storeId);
    }
}
