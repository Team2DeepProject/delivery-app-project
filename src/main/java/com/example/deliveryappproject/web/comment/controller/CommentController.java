package com.example.deliveryappproject.web.comment.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.Response;
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
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/reviews/{reviewId}/comments")
    @AuthPermission(role = UserRole.OWNER)
    public ResponseEntity<Void> createComment(
            @Auth AuthUser authUser,
            @PathVariable Long reviewId,
            @Valid @RequestBody CommentRequest request
    ) {
        commentService.createComment(authUser, reviewId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // 댓글 조회
    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<CommentResponse> getOwnerComment(
            @Auth AuthUser authUser,
            @PathVariable Long reviewId
    ) {
        CommentResponse commentResponse = commentService.getOwnerComment(reviewId, authUser);
        return ResponseEntity.ok(commentResponse);
    }

    // 댓글 수정
    @PutMapping("comments/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ) {
        CommentResponse updatedComment = commentService.updateComment(commentId, request);  // 댓글 수정
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}