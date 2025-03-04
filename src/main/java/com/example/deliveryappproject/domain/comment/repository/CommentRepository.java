package com.example.deliveryappproject.domain.comment.repository;

import com.example.deliveryappproject.domain.comment.entity.Comment;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    boolean existsByReviewAndUserIdAndUserRole(Review review, Long userId, UserRole userRole);

    Optional<Comment> findByReviewAndUserId(Review review, Long userId);
}
