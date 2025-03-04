package com.example.deliveryappproject.web.comment.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.auth.service.TokenService;
import com.example.deliveryappproject.domain.comment.dto.reponse.CommentResponse;
import com.example.deliveryappproject.domain.comment.dto.request.CommentRequest;
import com.example.deliveryappproject.domain.comment.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews/{reviewId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public CommentResponse createComment(
            @Auth AuthUser authUser,
            @PathVariable Long reviewId,
            @Valid @RequestBody CommentRequest request
    ) {
        return commentService.createComment(authUser, reviewId, request);
    }
}