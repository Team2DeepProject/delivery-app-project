package com.example.deliveryappproject.domain.comment.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.comment.dto.reponse.CommentResponse;
import com.example.deliveryappproject.domain.comment.dto.request.CommentRequest;
import com.example.deliveryappproject.domain.comment.entity.Comment;
import com.example.deliveryappproject.domain.comment.repository.CommentRepository;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.review.repository.ReviewRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    @Transactional
    public CommentResponse createComment(AuthUser authUser, Long reviewId, CommentRequest request) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BadRequestException("리뷰를 찾을 수 없습니다."));
        User user = new User(authUser.getId()); // 인증된 사용자 정보 사용

        Comment comment = Comment.builder()
                .review(review)
                .user(user)
                .content(request.getContent())
                .build();

        Comment savedComment = commentRepository.save(comment);
        return new CommentResponse(savedComment);
    }
}
