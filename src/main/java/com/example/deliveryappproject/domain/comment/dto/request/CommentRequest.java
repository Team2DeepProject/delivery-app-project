package com.example.deliveryappproject.domain.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CommentRequest {

    @NotEmpty(message = "댓글 내용을 입력해주세요.")
    private String content;

    public void updateContent(String content) {
        this.content = content;
    }
}
