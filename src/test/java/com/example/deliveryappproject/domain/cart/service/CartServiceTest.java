package com.example.deliveryappproject.domain.cart.service;

import com.example.deliveryappproject.domain.cart.dto.CartItemResponse;
import com.example.deliveryappproject.domain.cart.dto.CartItemsRequest;
import com.example.deliveryappproject.domain.cart.dto.CartResponse;
import com.example.deliveryappproject.domain.cart.model.CartItem;
import com.example.deliveryappproject.domain.cart.repository.CartRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.*;


@ExtendWith(MockitoExtension.class)
class CartServiceTest {


    @Mock
    private CartRepository cartRepository;

    @Mock
    private RedisLockService redisLockService;

    @InjectMocks
    private CartService cartService;

    @Nested
    class AddItems {
        @Test
        public void 장바구니추가_장바구니없음_초기화후저장_성공() {
            Long userId = 1L;
            CartItemsRequest cartItemsRequest = new CartItemsRequest();
            //given
            doAnswer(invocation -> {
                Runnable action = invocation.getArgument(1);
                action.run();
                return null;
            }).when(redisLockService).executeWithLock(any(Long.class), any(Runnable.class));

            given(cartRepository.findStoreId(any(Long.class))).willReturn(null);
            //when
            cartService.addItems(userId, cartItemsRequest);
            //then
            verify(cartRepository, times(1)).clear(any(Long.class));
            verify(cartRepository, times(1)).saveItems(any(Long.class), any(CartItemsRequest.class));
        }

        @Test
        public void 장바구니추가_다른가에에서추가_초기화후저장_성공() {
            //given
            Long userId = 1L;
            CartItemsRequest cartItemsRequest = new CartItemsRequest();
            ReflectionTestUtils.setField(cartItemsRequest,"storeId", 1L);

            doAnswer(invocation -> {
                Runnable action = invocation.getArgument(1);
                action.run();
                return null;
            }).when(redisLockService).executeWithLock(any(Long.class), any(Runnable.class));

            given(cartRepository.findStoreId(any(Long.class))).willReturn(2L);
            //when
            cartService.addItems(userId, cartItemsRequest);
            //then
            verify(cartRepository, times(1)).clear(any(Long.class));
            verify(cartRepository, times(1)).saveItems(any(Long.class), any(CartItemsRequest.class));
        }

        @Test
        public void 장바구니추가_같은가게에서추가_초기화없이저장_성공() {
            //given
            Long userId = 1L;
            CartItemsRequest cartItemsRequest = new CartItemsRequest();
            ReflectionTestUtils.setField(cartItemsRequest,"storeId", 1L);

            doAnswer(invocation -> {
                Runnable action = invocation.getArgument(1);
                action.run();
                return null;
            }).when(redisLockService).executeWithLock(any(Long.class), any(Runnable.class));

            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            //when
            cartService.addItems(userId, cartItemsRequest);
            //then
            verify(cartRepository, times(0)).clear(userId);
            verify(cartRepository, times(1)).saveItems(any(Long.class), any(CartItemsRequest.class));
        }
    }

    @Nested
    class GetItems {

        @Test
        public void 장바구니조회_장바구니비어있음_성공() {
            //given
            Long userId = 1L;
            given(cartRepository.findStoreId(any(Long.class))).willReturn(null);
            given(cartRepository.findItems(any(Long.class))).willReturn(List.of());
            //when
            CartResponse cartResponse = cartService.getItems(userId);
            //then
            Assertions.assertThat(cartResponse.getStoreId()).isNull();
            Assertions.assertThat(cartResponse.getItems()).isEmpty();

        }

        @Test
        public void 장바구니조회_장바구니에_아이템있음_성공() {
            //given
            Long userId = 1L;
            Long storeId = 1L;
            List<CartItem> cartItems = List.of(
                    new CartItem(1L, 2),
                    new CartItem(2L, 1)
            );
            List<CartItemResponse> cartItemResponses = cartItems.stream().map(CartItemResponse::from).toList();
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(cartRepository.findItems(any(Long.class))).willReturn(cartItems);
            //when
            CartResponse cartResponse = cartService.getItems(userId);
            //then
            Assertions.assertThat(cartResponse.getStoreId()).isEqualTo(1L);
            Assertions.assertThat(cartResponse.getItems().size()).isEqualTo(2);
        }
    }

    @Nested
    class ClearCart {

        @Test
        public void 장바구니초기화_성공() {

            //given
            Long userId = 1L;
            doNothing().when(cartRepository).clear(any(Long.class));
            //when
            cartService.clearCart(userId);
            //then
            verify(cartRepository, times(1)).clear(any(Long.class));
        }
    }

    @Nested
    class DeleteCartItem {

        @Test
        public void 장바구니_아이템_삭제_성공() {

            //given
            Long userId = 1L;
            Long itemId = 1L;
            doNothing().when(cartRepository).deleteItem(any(Long.class),any(Long.class));
            //when
            cartService.deleteCartItem(userId,itemId);
            //then
            verify(cartRepository, times(1)).deleteItem(any(Long.class),any(Long.class));
        }
    }

    @Nested
    class DecreaseItemQuantity {

        @Test
        public void 장바구니_아이템_수량_감소_성공() {

            //given
            Long userId = 1L;
            Long itemId = 1L;
            int quantity = 5;
            doNothing().when(cartRepository).decreaseItemQuantity(any(Long.class),any(Long.class),any(Integer.class));
            //when
            cartService.decreaseItemQuantity(userId,itemId,quantity);
            //then
            verify(cartRepository, times(1)).decreaseItemQuantity(any(Long.class),any(Long.class),any(Integer.class));
        }
    }


}