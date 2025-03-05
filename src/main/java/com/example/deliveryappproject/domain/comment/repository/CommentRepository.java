package com.example.deliveryappproject.domain.comment.repository;

import com.example.deliveryappproject.domain.comment.entity.Comment;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Comment c " +
            "WHERE c.review = :review AND c.user.id = :userId AND c.user.userRole = :userRole")
    boolean existsByReviewAndUserIdAndUserRole(@Param("review") Review review,
                                               @Param("userId") Long userId,
                                               @Param("userRole") UserRole userRole);
    
    Optional<Comment> findByReviewAndUser(Review review, User owner);
}
