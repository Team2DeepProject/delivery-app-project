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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void 사장님만_댓글을_달수_있다() throws NoSuchFieldException, IllegalAccessException {
        // Given
        Long reviewId = 1L;
        Long storeId = 1L;
        Long ownerId = 1L;

        // 사장님 사용자 생성 및 역할 설정
        User owner = new User(ownerId);
        owner.updateUserRole(UserRole.OWNER);
        AuthUser authUser = new AuthUser(ownerId, "owner@example.com", UserRole.OWNER);

        Store store = Store.builder()
                .user(owner)
                .storeName("Test Store")
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(21, 0))
                .minOrderPrice(BigDecimal.valueOf(10000))
                .build();

        store.getClass().getDeclaredField("id").set(store, storeId);

        Review review = Review.builder()
                .store(store)   // Store 객체 설정
                .user(owner)     // owner가 리뷰 작성자
                .content("맛있어요.")  // 리뷰 내용
                .rating(5)       // 별점
                .build();

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.updateContent("댓글 내용");

        // given: reviewId에 해당하는 리뷰가 존재하도록 설정
        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));

        // When
        commentService.createComment(authUser, reviewId, commentRequest);

        // Then: save()가 호출되었는지 확인하고, 저장된 Comment 객체를 캡처하여 검증
        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment savedComment = commentCaptor.getValue();
        assertNotNull(savedComment);
        assertEquals("댓글 내용", savedComment.getContent());
    }

    @Test
    void 사장님이_아닌_사용자는_댓글을_달수_없다() {
        // Given
        Long reviewId = 1L;
        Long storeId = 1L;
        Long userId = 2L;  // 사장님이 아닌 사용자

        // 일반 사용자 생성 및 역할 설정
        User user = new User(userId);
        user.updateUserRole(UserRole.USER); // 역할 설정
        AuthUser authUser = new AuthUser(userId, "user@example.com", UserRole.USER);

        // 사장님 사용자 생성 및 역할 설정 (리뷰를 작성한 사장님)
        Long ownerId = 1L;
        User owner = new User(ownerId);
        owner.updateUserRole(UserRole.OWNER); // 역할 설정

        Store store = Store.builder()
                .user(owner)  // 실제 사장님이 소유한 가게
                .storeName("Test Store")
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(21, 0))
                .minOrderPrice(BigDecimal.valueOf(10000))
                .build();

        // 리뷰 생성 (사장님이 작성한 리뷰)
        Review review = Review.builder()
                .store(store)   // Store 객체 설정
                .user(owner)    // 리뷰 작성자(사장님)
                .content("맛있어요.")  // 리뷰 내용
                .rating(5)       // 별점
                .build();

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.updateContent("댓글 내용");

        // given: reviewId에 해당하는 리뷰가 존재하도록 설정
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
        Long storeId = 1L;
        Long ownerId = 1L;

        // 사장님 사용자 생성 및 역할 설정
        User owner = new User(ownerId);
        owner.updateUserRole(UserRole.OWNER);
        AuthUser authUser = new AuthUser(ownerId, "owner@example.com", UserRole.OWNER);

        // Store 객체 생성
        Store store = Store.builder()
                .user(owner)
                .storeName("Test Store")
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(21, 0))
                .minOrderPrice(BigDecimal.valueOf(10000))
                .build();

        // 리뷰 생성
        Review review = Review.builder()
                .store(store)
                .user(owner)
                .content("맛있어요.")
                .rating(5)
                .build();

        // 댓글이 이미 존재한다고 설정
        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));
        given(commentRepository.existsByReviewAndUserIdAndUserRole(review, ownerId, UserRole.OWNER))
                .willReturn(true);  // 이미 댓글이 존재

        CommentRequest commentRequest = new CommentRequest();
        commentRequest.updateContent("댓글 내용");

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
        Long ownerId = 1L;
        Long userId = 1L;

        // 사장님 사용자 생성 및 역할 설정
        User owner = new User(ownerId);
        owner.updateUserRole(UserRole.OWNER);
        AuthUser authUser = new AuthUser(userId, "owner@example.com", UserRole.OWNER);

        // Store 객체 생성
        Store store = Store.builder()
                .user(owner)
                .storeName("Test Store")
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(21, 0))
                .minOrderPrice(BigDecimal.valueOf(10000))
                .build();

        // 리뷰 생성
        Review review = Review.builder()
                .store(store)
                .user(owner)
                .content("맛있어요.")
                .rating(5)
                .build();

        // 댓글 생성
        Comment comment = Comment.builder()
                .user(owner)
                .review(review)
                .content("댓글 내용")
                .build();

        // given: reviewId에 해당하는 리뷰와 댓글이 존재하도록 설정
        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));
        given(commentRepository.findByReviewAndUserId(review, ownerId))
                .willReturn(java.util.Optional.of(comment));

        // When
        CommentResponse response = commentService.getOwnerComment(reviewId, ownerId);

        // Then
        assertNotNull(response);
        assertEquals(ownerId, response.getUserId());
    }

    @Test
    void 사장님_댓글_조회_권한없을때() {
        // Given
        Long reviewId = 1L;
        Long storeId = 1L;
        Long userId = 2L;  // 다른 사용자

        // 사장님이 아닌 사용자 생성
        User owner = new User(userId);

        // Store 객체 생성
        Store store = Store.builder()
                .user(owner)
                .storeName("Test Store")
                .openAt(LocalTime.of(9, 0))
                .closeAt(LocalTime.of(21, 0))
                .minOrderPrice(BigDecimal.valueOf(10000))
                .build();

        // 리뷰 생성
        Review review = Review.builder()
                .store(store)
                .user(owner)
                .content("맛있어요.")
                .rating(5)
                .build();

        // given: reviewId에 해당하는 리뷰가 존재하도록 설정
        given(reviewRepository.findById(reviewId)).willReturn(java.util.Optional.of(review));

        // When & Then
        BadRequestException exception = assertThrows(BadRequestException.class,
                () -> commentService.getOwnerComment(reviewId, userId));
        assertEquals("이 리뷰에 대한 사장님의 댓글을 조회할 권한이 없습니다.", exception.getMessage());
    }
}