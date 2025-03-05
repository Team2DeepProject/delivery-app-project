package com.example.deliveryappproject.domain.comment.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "comments")
public class Comment extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 댓글을 단 사용자

    @Column(nullable = false)
    private String content;

    @Builder
    public Comment(Review review, User user, String content) {
        this.review = review;
        this.user = user;
        this.content = content;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
