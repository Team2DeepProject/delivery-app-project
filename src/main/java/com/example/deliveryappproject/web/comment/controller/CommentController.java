package com.example.deliveryappproject.web.comment.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.auth.service.TokenService;
import com.example.deliveryappproject.domain.comment.dto.reponse.CommentResponse;
import com.example.deliveryappproject.domain.comment.dto.request.CommentRequest;
import com.example.deliveryappproject.domain.comment.service.CommentService;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping
    public ResponseEntity<Void> createComment(
            @Auth AuthUser authUser,
            @PathVariable Long reviewId,
            @Valid @RequestBody CommentRequest request
    ) {
        commentService.createComment(authUser, reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();  // 응답 없음
    }

    // 댓글 조회
    @GetMapping("/owner/{userId}")
    public ResponseEntity<CommentResponse> getOwnerComment(
            @PathVariable Long reviewId,
            @PathVariable Long userId
    ) {
        CommentResponse commentResponse = commentService.getOwnerComment(reviewId, userId);
        return ResponseEntity.ok(commentResponse);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        commentService.updateComment(commentId, request);
        return ResponseEntity.ok().build();
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}