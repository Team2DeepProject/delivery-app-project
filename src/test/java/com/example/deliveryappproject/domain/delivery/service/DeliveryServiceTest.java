package com.example.deliveryappproject.domain.delivery.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.delivery.dto.DeliveryResponse;
import com.example.deliveryappproject.domain.delivery.entity.Delivery;
import com.example.deliveryappproject.domain.delivery.entity.DeliveryStatus;
import com.example.deliveryappproject.domain.delivery.repository.DeliveryRepository;
import com.example.deliveryappproject.domain.order.entity.Order;
import com.example.deliveryappproject.domain.order.entity.OrderStatus;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    @Nested
    class StartDelivery {

        @Test
        public void 배송시작_존재하지않는_배송_NotFoundException_예외_발생() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;

            given(deliveryRepository.findById(any(Long.class))).willThrow(NotFoundException.class);
            //when
            assertThrows(NotFoundException.class, () -> deliveryService.startDelivery(userId, deliveryId));
            //then

        }

        @Test
        public void 배송시작_주문상태_ACCEPT_아님_BadRequestException_예외_발생() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 0), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            Delivery delivery = new Delivery(order);
            ReflectionTestUtils.setField(delivery, "deliveryStatus", DeliveryStatus.READY);
            given(deliveryRepository.findById(any(Long.class))).willReturn(Optional.of(delivery));
            //when
            BadRequestException e = assertThrows(BadRequestException.class, () -> deliveryService.startDelivery(userId, deliveryId));

            assertEquals(e.getMessage(),"Order is not accept");
            //then

        }

        @Test
        public void 배송시작_배송상태_PENDING_아님_BadRequestException_예외_발생() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 0), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            order.acceptOrder();
            Delivery delivery = new Delivery(order);
            ReflectionTestUtils.setField(delivery, "deliveryStatus", DeliveryStatus.READY);
            given(deliveryRepository.findById(any(Long.class))).willReturn(Optional.of(delivery));
            //when
            BadRequestException e = assertThrows(BadRequestException.class, () -> deliveryService.startDelivery(userId, deliveryId));

            assertEquals(e.getMessage(),"Delivery is not pending");
            //then

        }

        @Test
        public void 배송시작_성공() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 0), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            order.acceptOrder();
            Delivery delivery = new Delivery(order);
            given(deliveryRepository.findById(any(Long.class))).willReturn(Optional.of(delivery));
            //when
            deliveryService.startDelivery(userId, deliveryId);

            //then
            assertEquals(delivery.getDeliveryStatus(), DeliveryStatus.READY);
        }
    }

    @Nested
    class CompleteDelivery {

        @Test
        public void 배송완료_존재하지않는_배송_NotFoundException_예외_발생() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;

            given(deliveryRepository.findById(any(Long.class))).willThrow(NotFoundException.class);
            //when
            assertThrows(NotFoundException.class, () -> deliveryService.completeDelivery(userId, deliveryId));
            //then

        }

        @Test
        public void 배송완료_배송상태_READY_아님_BadRequestException_예외_발생() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 0), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            Delivery delivery = new Delivery(order);
            given(deliveryRepository.findById(any(Long.class))).willReturn(Optional.of(delivery));
            //when
            BadRequestException e = assertThrows(BadRequestException.class, () -> deliveryService.completeDelivery(userId, deliveryId));

            assertEquals(e.getMessage(),"Delivery is not ready");
            //then

        }

        @Test
        public void 배송완료_담당자_아님_ForbiddenException_예외_발생() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 0), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            Delivery delivery = new Delivery(order);
            ReflectionTestUtils.setField(delivery,"id", 2L);
            ReflectionTestUtils.setField(delivery,"deliveryStatus", DeliveryStatus.READY);
            given(deliveryRepository.findById(any(Long.class))).willReturn(Optional.of(delivery));
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> deliveryService.completeDelivery(userId, deliveryId));

            assertEquals(e.getMessage(),"해당 배송을 맡은 유저가 아닙니다.");
            //then

        }

        @Test
        public void 배송완료_성공() {
            //given
            Long userId = 1L;
            Long deliveryId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 0), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            Delivery delivery = new Delivery(order);
            ReflectionTestUtils.setField(delivery,"deliveryUserId", 1L);
            ReflectionTestUtils.setField(delivery,"deliveryStatus", DeliveryStatus.READY);
            given(deliveryRepository.findById(any(Long.class))).willReturn(Optional.of(delivery));
            //when
            DeliveryResponse deliveryResponse = deliveryService.completeDelivery(userId, deliveryId);

            //then
            assertEquals(delivery.getDeliveryStatus(), DeliveryStatus.COMP);
            assertEquals(deliveryResponse.getOrderStatus(), OrderStatus.COMP.name());
        }

    }
}