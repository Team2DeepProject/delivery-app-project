package com.example.deliveryappproject.domain.comment.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.comment.dto.reponse.CommentResponse;
import com.example.deliveryappproject.domain.comment.dto.request.CommentRequest;
import com.example.deliveryappproject.domain.comment.entity.Comment;
import com.example.deliveryappproject.domain.comment.repository.CommentRepository;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.review.repository.ReviewRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    // 댓글 생성
    @Transactional
    public void createComment(AuthUser authUser, Long reviewId, CommentRequest request) {
        // 리뷰 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BadRequestException("리뷰를 찾을 수 없습니다."));

        // 해당 가게의 사장님이 맞는지 확인
        if (!review.getStore().getUser().getId().equals(authUser.getId())) {
            throw new BadRequestException("해당 가게의 사장님만 댓글을 달 수 있습니다.");
        }

        // 이미 사장님 댓글이 있는지 확인
        if (commentRepository.existsByReviewAndUserIdAndUserRole(review, authUser.getId(), UserRole.OWNER)) {
            throw new BadRequestException("리뷰에 대한 사장님 댓글은 하나만 작성할 수 있습니다.");
        }

        // 댓글 생성
        User user = new User(authUser.getId()); // 인증된 사용자 정보 사용
        Comment comment = Comment.builder()
                .review(review)
                .user(user)
                .content(request.getContent())
                .userRole(UserRole.OWNER)  // 역할 설정
                .build();

        Comment savedComment = commentRepository.save(comment);
    }

    // 사장님 댓글 조회
    @Transactional
    public CommentResponse getOwnerComment(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BadRequestException("리뷰를 찾을 수 없습니다."));

        Store store = review.getStore();
        User owner = store.getUser();  // EAGER 로딩

        if (owner.getId().equals(userId)) {
            Comment comment = commentRepository.findByReviewAndUserId(review, userId)
                    .orElseThrow(() -> new BadRequestException("사장님의 댓글을 찾을 수 없습니다."));

            return new CommentResponse(comment);
        } else {
            throw new BadRequestException("이 리뷰에 대한 사장님의 댓글을 조회할 권한이 없습니다.");
        }
    }

    // 댓글 수정
    @Transactional
    public CommentResponse updateComment(Long commentId, CommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("댓글을 찾을 수 없습니다."));

        comment.updateContent(request.getContent()); // 댓글 내용 수정
        return new CommentResponse(comment);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BadRequestException("댓글을 찾을 수 없습니다."));

        commentRepository.delete(comment);
    }
}