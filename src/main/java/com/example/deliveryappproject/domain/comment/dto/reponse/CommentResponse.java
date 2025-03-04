package com.example.deliveryappproject.domain.comment.dto.reponse;

import com.example.deliveryappproject.domain.comment.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private Long id;
    private String content;
    private Long userId;
    private final LocalDateTime createdAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.userId = comment.getUser().getId();
        this.createdAt = comment.getCreatedAt();
    }
}
