package com.example.deliveryappproject.domain.comment.service;

import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private CommentService commentService;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

    private User owner;
    private User user;
    private Store store;
    private Review review;

    @BeforeEach
    void setUp() {
        Long ownerId = 1L;
        Long userId = 2L;
        Long storeId = 1L;

        // 기본 사장님과 일반 사용자 생성
        owner = createUser(ownerId, UserRole.OWNER);
        user = createUser(userId, UserRole.USER);

        // 가게, 리뷰 객체 생성
        store = createStore(owner, storeId);
        review = createReview(store, owner);
    }

    private User createUser(Long userId, UserRole role) {
        User newUser = new User(userId);
        newUser.updateUserRole(role);
        return newUser;
    }

    private Store createStore(User owner, Long storeId) {
        Store store = Store.builder()
                .user(owner)
                .storeName("Test Store")
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(21, 0))
                .minOrderPrice(BigDecimal.valueOf(10000))
                .build();
        try {
            store.getClass().getDeclaredField("id").set(store, storeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return store;
    }

    private Review createReview(Store store, User owner) {
        return Review.builder()
                .store(store)
                .user(owner)
                .content("맛있어요.")
                .rating(5)
                .build();
    }

    @Test
    void 사장님만_댓글을_달수_있다() {
        // Given
        Long reviewId = 1L;
        AuthUser authUser = new AuthUser(owner.getId(), "owner@example.com", UserRole.OWNER);
        CommentRequest commentRequest = createCommentRequest("댓글 내용");

        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));

        // When
        commentService.createComment(authUser, reviewId, commentRequest);

        // Then
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();
        assertNotNull(savedComment);
        assertEquals("댓글 내용", savedComment.getContent());
    }

    @Test
    void 사장님이_아닌_사용자는_댓글을_달수_없다() {
        // Given
        Long reviewId = 1L;
        AuthUser authUser = new AuthUser(user.getId(), "user@example.com", UserRole.USER);
        CommentRequest commentRequest = createCommentRequest("댓글 내용");

        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> commentService.createComment(authUser, reviewId, commentRequest));
        assertEquals("해당 가게의 사장님만 댓글을 달 수 있습니다.", exception.getMessage());
        verify(commentRepository, never()).save(commentCaptor.capture());
    }

    @Test
    void 사장님이_댓글을_이미_달았으면_다시_달수_없다() {
        // Given
        Long reviewId = 1L;
        AuthUser authUser = new AuthUser(owner.getId(), "owner@example.com", UserRole.OWNER);
        CommentRequest commentRequest = createCommentRequest("댓글 내용");

        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));
        given(commentRepository.existsByReviewAndUserIdAndUserRole(review, owner.getId(), UserRole.OWNER))
                .willReturn(true);  // 이미 댓글이 존재

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> commentService.createComment(authUser, reviewId, commentRequest));
        assertEquals("리뷰에 대한 사장님 댓글은 하나만 작성할 수 있습니다.", exception.getMessage());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void 사장님_댓글_조회_테스트() {
        // Given
        Long reviewId = 1L;
        AuthUser authUser = new AuthUser(owner.getId(), "owner@example.com", UserRole.OWNER);

        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));
        given(commentRepository.findByReviewAndUserId(review, owner.getId()))
                .willReturn(java.util.Optional.of(createComment(owner, review)));

        // When
        CommentResponse response = commentService.getOwnerComment(reviewId, owner.getId());

        // Then
        assertNotNull(response);
        assertEquals(owner.getId(), response.getUserId());
    }

    @Test
    void 사장님_댓글_조회_권한없을때() {
        // Given
        Long reviewId = 1L;
        Long userId = 2L;  // 다른 사용자
        AuthUser authUser = new AuthUser(userId, "user@example.com", UserRole.USER);

        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> commentService.getOwnerComment(reviewId, userId));
        assertEquals("이 리뷰에 대한 사장님의 댓글을 조회할 권한이 없습니다.", exception.getMessage());
    }

    private CommentRequest createCommentRequest(String content) {
        CommentRequest commentRequest = new CommentRequest();
        commentRequest.updateContent(content);
        return commentRequest;
    }

    private Comment createComment(User user, Review review) {
        return Comment.builder()
                .user(user)
                .review(review)
                .content("댓글 내용")
                .build();
    }
}
