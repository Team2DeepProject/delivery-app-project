package com.example.deliveryappproject.domain.review.service;

import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.review.dto.request.ReviewCreateRequest;
import com.example.deliveryappproject.domain.review.dto.request.ReviewUpdateRequest;
import com.example.deliveryappproject.domain.review.dto.response.ReviewResponse;
import com.example.deliveryappproject.domain.review.entity.Review;
import com.example.deliveryappproject.domain.review.repository.ReviewRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private AuthUser authUser;
    private User user;
    private Store store;
    private Review review;

    @BeforeEach
    void setUp() {
        // 공통 객체 설정
        authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        user = new User("email@email.com", "password", "홍길동", UserRole.USER);
        store = new Store(user, "한식가게", LocalTime.of(10, 0), LocalTime.of(22, 0), BigDecimal.valueOf(10000));

        review = Review.builder()
                .store(store)
                .user(user)
                .content("맛있어요")
                .rating(5)
                .build();
    }

    @Test
    void 리뷰작성_성공() {
        // given
        ReviewCreateRequest request = new ReviewCreateRequest("맛있어요", 5);
        given(storeRepository.findById(1L)).willReturn(Optional.of(store));
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        ReviewResponse response = reviewService.createReview(authUser, 1L, request);

        // then
        assertEquals("맛있어요", response.getContent());
        assertEquals(5, response.getRating());
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void 리뷰작성_가게없음_실패() {
        // given
        ReviewCreateRequest request = new ReviewCreateRequest("맛있어요", 5);
        given(storeRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> reviewService.createReview(authUser, 1L, request),
                "가게를 찾을 수 없습니다.");
    }

    @Test
    void 리뷰수정_성공() {
        // given
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest("더 맛있어요", 4);
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // when
        Review updatedReview = reviewService.updateReview(authUser, 1L, updateRequest);

        // then
        assertEquals("더 맛있어요", updatedReview.getContent());
        assertEquals(4, updatedReview.getRating());
    }

    @Test
    void 리뷰수정_권한없음_실패() {
        // given
        AuthUser anotherUser = new AuthUser(2L, "anotherEmail@email.com", UserRole.USER);
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest("더 맛있어요", 4);
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> reviewService.updateReview(anotherUser, 1L, updateRequest),
                "본인의 리뷰만 수정할 수 있습니다.");
    }

    /* deleteReview */
    @Test
    void 리뷰삭제_성공() {
        // given
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // when
        reviewService.deleteReview(authUser, 1L);

        // then
        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    void 리뷰삭제_권한없음_실패() {
        // given
        AuthUser anotherUser = new AuthUser(2L, "anotherEmail@email.com", UserRole.USER);
        given(reviewRepository.findById(1L)).willReturn(Optional.of(review));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> reviewService.deleteReview(anotherUser, 1L),
                "본인의 리뷰만 삭제할 수 있습니다.");
    }

    @Test
    void 리뷰삭제_리뷰없음_실패() {
        // given
        given(reviewRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> reviewService.deleteReview(authUser, 1L),
                "리뷰를 찾을 수 없습니다.");
    }
}
