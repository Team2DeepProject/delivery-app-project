package com.example.deliveryappproject.domain.order.service;

import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.cart.model.CartItem;
import com.example.deliveryappproject.domain.cart.repository.CartRepository;
import com.example.deliveryappproject.domain.delivery.entity.Delivery;
import com.example.deliveryappproject.domain.delivery.repository.DeliveryRepository;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.repository.MenuRepository;
import com.example.deliveryappproject.domain.order.dto.OrderDetailResponse;
import com.example.deliveryappproject.domain.order.dto.OrderRequest;
import com.example.deliveryappproject.domain.order.entity.Order;
import com.example.deliveryappproject.domain.order.entity.OrderStatus;
import com.example.deliveryappproject.domain.order.repository.OrderRepository;
import com.example.deliveryappproject.domain.order.service.dto.OrderResponse;
import com.example.deliveryappproject.domain.policy.PointPolicy;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.service.StoreService;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.domain.user.service.UserService;
import com.example.deliveryappproject.domain.user.userpoint.PointHistoryRepository;
import com.example.deliveryappproject.domain.user.userpoint.entity.PointHistory;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.MIN_ORDER_AMOUNT_REQUIRED;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.ORDER_NOT_AVAILABLE;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.ORDER_NOT_OWNER;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.ORDER_STATUS_NOT_PENDING;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.POINT_NOT_ENOUGH;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private  CartRepository cartRepository;
    @Mock
    private  MenuRepository menuRepository;
    @Mock
    private  OrderRepository orderRepository;
    @Mock
    private  DeliveryRepository deliveryRepository;
    @Mock
    private  PointHistoryRepository pointHistoryRepository;
    @Mock
    private  PointPolicy pointPolicy;
    @Mock
    private  StoreService storeService;
    @Mock
    private  UserService userService;

    @InjectMocks
    private OrderService orderService;

    @Nested
    class CreateOrder {

        @Test
        public void 주문생성_장바구니_비어있음_NotFoundException_예외_발생_실패() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();

            given(cartRepository.findStoreId(any(Long.class))).willReturn(null);
            //when & then
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.order(userId, orderRequest));

            assertEquals(e.getMessage(), "cart is empty");

        }

        @Test
        public void 주문생성_존재하지_않는_가게_NotFoundException_예외_발생_실패() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();

            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willThrow(NotFoundException.class);
            //when & then
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.order(userId, orderRequest));

        }

        @Test
        public void 주문생성_가게_주문_가능시간_아님_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(0, 1), BigDecimal.valueOf(20000));
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willReturn(store);
            //when & then
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.order(userId, orderRequest));
            assertEquals(e.getMessage(),ORDER_NOT_AVAILABLE);
        }

        @Test
        public void 주문생성_장바구니_메뉴_일치하지않음_NotFoundException_예외_발생_실패() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            List<CartItem> items = List.of(
                    new CartItem(1L, 1),
                    new CartItem(2L, 2),
                    new CartItem(3L, 1)
            );
            Menu menu1 = new Menu("메뉴1", BigDecimal.valueOf(3000), "소개1", store);
            Menu menu2 = new Menu("메뉴2", BigDecimal.valueOf(3000), "소개1", store);
            ReflectionTestUtils.setField(menu1,"id",1L);
            ReflectionTestUtils.setField(menu2,"id",4L);
            List<Menu> menus = List.of(
                    menu1,
                    menu2
            );
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willReturn(store);
            given(cartRepository.findItems(any(Long.class))).willReturn(items);
            given(menuRepository.findAllById(any(Iterable.class))).willReturn(menus);
            //when & then
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.order(userId, orderRequest));
            assertTrue(e.getMessage().startsWith("menu not found:"));
        }

        @Test
        public void 주문생성_최소주문금액_미달_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            List<CartItem> items = List.of(
                    new CartItem(1L, 1),
                    new CartItem(2L, 2),
                    new CartItem(3L, 1)
            );
            Menu menu1 = new Menu("메뉴1", BigDecimal.valueOf(3000), "소개1", store);
            Menu menu2 = new Menu("메뉴2", BigDecimal.valueOf(3000), "소개1", store);
            Menu menu3 = new Menu("메뉴3", BigDecimal.valueOf(3000), "소개1", store);
            ReflectionTestUtils.setField(menu1,"id",1L);
            ReflectionTestUtils.setField(menu2,"id",2L);
            ReflectionTestUtils.setField(menu3,"id",3L);
            List<Menu> menus = List.of(
                    menu1,
                    menu2,
                    menu3
            );
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willReturn(store);
            given(cartRepository.findItems(any(Long.class))).willReturn(items);
            given(menuRepository.findAllById(any(Iterable.class))).willReturn(menus);
            //when & then
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.order(userId, orderRequest));
            assertEquals(e.getMessage(), MIN_ORDER_AMOUNT_REQUIRED);
        }

        @Test
        public void 주문생성_존재하지않는_유저_예외_발생_실패() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            List<CartItem> items = List.of(
                    new CartItem(1L, 1),
                    new CartItem(2L, 2),
                    new CartItem(3L, 1)
            );
            Menu menu1 = new Menu("메뉴1", BigDecimal.valueOf(10000), "소개1", store);
            Menu menu2 = new Menu("메뉴2", BigDecimal.valueOf(20000), "소개1", store);
            Menu menu3 = new Menu("메뉴3", BigDecimal.valueOf(3000), "소개1", store);
            ReflectionTestUtils.setField(menu1,"id",1L);
            ReflectionTestUtils.setField(menu2,"id",2L);
            ReflectionTestUtils.setField(menu3,"id",3L);
            List<Menu> menus = List.of(
                    menu1,
                    menu2,
                    menu3
            );
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willReturn(store);
            given(cartRepository.findItems(any(Long.class))).willReturn(items);
            given(menuRepository.findAllById(any(Iterable.class))).willReturn(menus);
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willThrow(NotFoundException.class);
            //when & then
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.order(userId, orderRequest));
        }

        @Test
        public void 주문생성_포인트_미달_예외_발생_실패() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();
            ReflectionTestUtils.setField(orderRequest,"usePoints", 10);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            List<CartItem> items = List.of(
                    new CartItem(1L, 1),
                    new CartItem(2L, 2),
                    new CartItem(3L, 1)
            );
            Menu menu1 = new Menu("메뉴1", BigDecimal.valueOf(10000), "소개1", store);
            Menu menu2 = new Menu("메뉴2", BigDecimal.valueOf(20000), "소개1", store);
            Menu menu3 = new Menu("메뉴3", BigDecimal.valueOf(3000), "소개1", store);
            ReflectionTestUtils.setField(menu1,"id",1L);
            ReflectionTestUtils.setField(menu2,"id",2L);
            ReflectionTestUtils.setField(menu3,"id",3L);
            List<Menu> menus = List.of(
                    menu1,
                    menu2,
                    menu3
            );
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willReturn(store);
            given(cartRepository.findItems(any(Long.class))).willReturn(items);
            given(menuRepository.findAllById(any(Iterable.class))).willReturn(menus);
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willReturn(user);
            //when & then
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.order(userId, orderRequest));
            assertEquals(e.getMessage(),POINT_NOT_ENOUGH);
        }

        @Test
        public void 주문생성_포인트_미사용_성공() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();
//            ReflectionTestUtils.setField(orderRequest,"usePoints", 10);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            List<CartItem> items = List.of(
                    new CartItem(1L, 1),
                    new CartItem(2L, 2),
                    new CartItem(3L, 1)
            );
            Menu menu1 = new Menu("메뉴1", BigDecimal.valueOf(10000), "소개1", store);
            Menu menu2 = new Menu("메뉴2", BigDecimal.valueOf(20000), "소개1", store);
            Menu menu3 = new Menu("메뉴3", BigDecimal.valueOf(3000), "소개1", store);
            ReflectionTestUtils.setField(menu1,"id",1L);
            ReflectionTestUtils.setField(menu2,"id",2L);
            ReflectionTestUtils.setField(menu3,"id",3L);
            List<Menu> menus = List.of(
                    menu1,
                    menu2,
                    menu3
            );
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            ReflectionTestUtils.setField(user, "point", 100);
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willReturn(store);
            given(cartRepository.findItems(any(Long.class))).willReturn(items);
            given(menuRepository.findAllById(any(Iterable.class))).willReturn(menus);
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willReturn(user);
            //when & then
            OrderResponse order = orderService.order(userId, orderRequest);

            assertEquals(order.getOrderStatus(), OrderStatus.PENDING.name());
        }

        @Test
        public void 주문생성_포인트사용_성공() {
            //given
            Long userId = 1L;
            OrderRequest orderRequest = new OrderRequest();
            ReflectionTestUtils.setField(orderRequest,"usePoints", 10);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            List<CartItem> items = List.of(
                    new CartItem(1L, 1),
                    new CartItem(2L, 2),
                    new CartItem(3L, 1)
            );
            Menu menu1 = new Menu("메뉴1", BigDecimal.valueOf(10000), "소개1", store);
            Menu menu2 = new Menu("메뉴2", BigDecimal.valueOf(20000), "소개1", store);
            Menu menu3 = new Menu("메뉴3", BigDecimal.valueOf(3000), "소개1", store);
            ReflectionTestUtils.setField(menu1,"id",1L);
            ReflectionTestUtils.setField(menu2,"id",2L);
            ReflectionTestUtils.setField(menu3,"id",3L);
            List<Menu> menus = List.of(
                    menu1,
                    menu2,
                    menu3
            );
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            ReflectionTestUtils.setField(user, "point", 100);
            given(cartRepository.findStoreId(any(Long.class))).willReturn(1L);
            given(storeService.findStoreByIdOrElseThrow(any(Long.class))).willReturn(store);
            given(cartRepository.findItems(any(Long.class))).willReturn(items);
            given(menuRepository.findAllById(any(Iterable.class))).willReturn(menus);
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willReturn(user);
            //when & then
            OrderResponse order = orderService.order(userId, orderRequest);

            assertEquals(order.getOrderStatus(), OrderStatus.PENDING.name());
        }
    }

    @Nested
    class AcceptOrder {

        @Test
        public void 주문수락_존재하지_않는_주문_NotFoundException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;

            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willThrow(NotFoundException.class);
            //when
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.acceptOrder(userId, orderId));
            //then
        }

        @Test
        public void 주문수락_가게_사장님_아님_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            order.cancelOrder();
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.acceptOrder(userId,  orderId));
            //then
            assertEquals(e.getMessage(),"해당 주문에 대한 권한이 없습니다.");
        }

        @Test
        public void 주문수락_Pending_상태_아님_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            order.cancelOrder();
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.acceptOrder(userId, orderId));
            //then
            assertEquals(e.getMessage(),ORDER_STATUS_NOT_PENDING);
        }

        @Test
        public void 주문수락_존재하지않는_유저_NotFoundException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willThrow(NotFoundException.class);
            //when
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.acceptOrder(userId, orderId));
            //then
        }

        @Test
        public void 주문수락_포인트_사용_포인트_부족_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willReturn(user);
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.acceptOrder(userId, orderId));
            //then
            assertEquals(e.getMessage(), POINT_NOT_ENOUGH);
        }

        @Test
        public void 주문수락_포인트_사용_포인트_히스토리_생성_성공() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            ReflectionTestUtils.setField(user,"point",100);
            Order order = new Order(user,store,100);
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willReturn(user);
            //when
            OrderResponse orderResponse = orderService.acceptOrder(userId, orderId);
            //then
            assertEquals(user.getPoint(),0);
            assertEquals(orderResponse.getOrderStatus(),OrderStatus.ACCEPT.name());

            verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
            verify(deliveryRepository, times(1)).save(any(Delivery.class));
        }

        @Test
        public void 주문수락_포인트_미사용_포인트_히스토리_생성_성공() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,0);
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            given(userService.findUserByIdOrElseThrow(any(Long.class))).willReturn(user);
            given(pointPolicy.calculateEarnedPoints(any(BigDecimal.class))).willReturn(100);
            //when
            OrderResponse orderResponse = orderService.acceptOrder(userId, orderId);
            //then
            assertEquals(orderResponse.getOrderStatus(),OrderStatus.ACCEPT.name());
            assertEquals(user.getPoint(), 100);
            verify(pointHistoryRepository, times(1)).save(any(PointHistory.class));
            verify(deliveryRepository, times(1)).save(any(Delivery.class));
            verify(pointPolicy, times(1)).calculateEarnedPoints(any(BigDecimal.class));

        }
    }

    @Nested
    class RejectOrder {

        @Test
        public void 주문거절_존재하지_않는_주문_NotFoundException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;

            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willThrow(NotFoundException.class);
            //when
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.rejectOrder(userId, orderId));
            //then
        }

        @Test
        public void 주문거절_가게_사장님_아님_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            order.cancelOrder();
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.rejectOrder(userId,  orderId));
            //then
            assertEquals(e.getMessage(),"해당 주문에 대한 권한이 없습니다.");
        }

        @Test
        public void 주문거절_Pending_상태_아님_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            order.cancelOrder();
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.rejectOrder(userId,  orderId));
            //then
            assertEquals(e.getMessage(),ORDER_STATUS_NOT_PENDING);
        }
        @Test
        public void 주문거절_성공() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            User owner = new User("asd2@asd.com","1q2w3e4r!","mock", UserRole.OWNER);
            ReflectionTestUtils.setField(owner,"id",1L);
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            ReflectionTestUtils.setField(store,"user",owner);
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,0);
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            OrderResponse orderResponse = orderService.rejectOrder(userId, orderId);
            //then
            assertEquals(orderResponse.getOrderStatus(),OrderStatus.REJECT.name());

        }
    }

    @Nested
    class CancelOrder {
        @Test
        public void 주문취소_존재하지_않는_주문_NotFoundException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;

            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willThrow(NotFoundException.class);
            //when
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.cancelOrder(userId, orderId));
            //then
        }

        @Test
        public void 주문취소_Pending_상태_아님_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            Order order = new Order(user,store,100);
            order.cancelOrder();
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.cancelOrder(userId, orderId));
            //then
            assertEquals(e.getMessage(),ORDER_STATUS_NOT_PENDING);
        }

        @Test
        public void 주문취소_주문자_아님_ForbiddenException_예외_발생_실패() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            ReflectionTestUtils.setField(user,"id",2L);
            Order order = new Order(user,store,0);
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            ForbiddenException e = assertThrows(ForbiddenException.class, () -> orderService.cancelOrder(userId, orderId));
            //then
            assertEquals(e.getMessage(), ORDER_NOT_OWNER);

        }

        @Test
        public void 주문취소_성공() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            ReflectionTestUtils.setField(user,"id",1L);
            Order order = new Order(user,store,0);
            given(orderRepository.findById(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            OrderResponse orderResponse = orderService.cancelOrder(userId, orderId);
            //then
            assertEquals(orderResponse.getOrderStatus(), OrderStatus.CANCEL.name());
        }
    }

    @Nested
    class GetOrder {

        @Test
        public void 주문상세_존재하지_않는_주문_예외_발생() {
            //given
            Long orderId = 1L;

            given(orderRepository.findByIdWithStoreWithOrderItems(ArgumentMatchers.any(Long.class))).willThrow(NotFoundException.class);
            //when
            NotFoundException e = assertThrows(NotFoundException.class, () -> orderService.getOrder(orderId));
            //then

        }

        @Test
        public void 주문상세_조회_성공() {
            //given
            Long userId = 1L;
            Long orderId = 1L;
            Store store = new Store(1L, "한식가게", LocalTime.of(0, 0), LocalTime.of(23, 59), BigDecimal.valueOf(20000));
            User user = new User("asd@asd.com","1q2w3e4r!","mock", UserRole.USER);
            ReflectionTestUtils.setField(user,"id",1L);
            Order order = new Order(user,store,0);

            given(orderRepository.findByIdWithStoreWithOrderItems(ArgumentMatchers.any(Long.class))).willReturn(Optional.of(order));
            //when
            OrderDetailResponse orderDetailResponse = orderService.getOrder(orderId);
            //then
            assertEquals(store.getId(), orderDetailResponse.getStoreResponse().getStoreId());
        }
    }
}