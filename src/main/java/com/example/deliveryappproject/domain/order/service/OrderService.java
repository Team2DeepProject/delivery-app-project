package com.example.deliveryappproject.domain.order.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
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
import com.example.deliveryappproject.domain.order.entity.OrderItem;
import com.example.deliveryappproject.domain.order.repository.OrderRepository;
import com.example.deliveryappproject.domain.order.service.dto.OrderResponse;
import com.example.deliveryappproject.domain.policy.PointPolicy;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.service.StoreService;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.service.UserService;
import com.example.deliveryappproject.domain.user.userpoint.PointHistoryRepository;
import com.example.deliveryappproject.domain.user.userpoint.entity.PointHistory;
import com.example.deliveryappproject.domain.user.userpoint.entity.PointType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.MIN_ORDER_AMOUNT_REQUIRED;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.ORDER_NOT_AVAILABLE;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.ORDER_NOT_FOUND;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.ORDER_NOT_OWNER;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.ORDER_STATUS_NOT_PENDING;
import static com.example.deliveryappproject.domain.order.exception.ErrorMessages.POINT_NOT_ENOUGH;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final PointPolicy pointPolicy;
    private final StoreService storeService;
    private final UserService userService;


    @Transactional
    public OrderResponse order(Long userId, OrderRequest orderRequest) {

        Long storeId = cartRepository.findStoreId(userId);

        if (storeId == null) {
            throw new NotFoundException("cart is empty");
        }

        Store store = storeService.findStoreByIdOrElseThrow(storeId);

        validateOrderAvailability(store);

        List<CartItem> items = cartRepository.findItems(userId);
        List<Long> itemIds = items.stream()
                .map(item -> item.getItemId())
                .toList();

        List<Menu> menus = menuRepository.findAllById(itemIds);
        Map<Long, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, menu -> menu));

        BigDecimal totalPrice = calculateTotalPrice(menuMap, items);

        validateMinOrderAmount(store, totalPrice);


        //포인트 사용 검증
        User user = userService.findUserByIdOrElseThrow(userId);
        validateUsePoints(user, orderRequest.getUsePoints());

        Order order = new Order(user, store, orderRequest.getUsePoints());

        convertAndAddOrderItems(order,menuMap, items);


        orderRepository.save(order);

        //장바구니 비우기
        cartRepository.clear(userId);


        return OrderResponse.of(order.getId(),storeId, order.getOrderStatus());

    }

    private void validateUsePoints(User user, int usePoints) {
        if (usePoints > 0 && usePoints > user.getPoint()) {
            throw new ForbiddenException(POINT_NOT_ENOUGH);
        }
    }


    @Transactional
    public OrderResponse acceptOrder(Long userId, Long orderId) {
        Order order = findByIdOrElseThrow(orderId);

        validateStoreOwner(order, userId);
        validateOrderStatusPending(order);

        User user = userService.findUserByIdOrElseThrow(userId);

        validateUsePoints(user, order.getUsedPoints());

        handlePoints(order, user);

        order.acceptOrder();

        Delivery delivery = new Delivery(order);
        deliveryRepository.save(delivery);

        return OrderResponse.of(order.getId(),order.getStore().getId(), order.getOrderStatus());

    }

    @Transactional
    public OrderResponse rejectOrder(Long userId, Long orderId) {
        Order order = findByIdOrElseThrow(orderId);

        validateStoreOwner(order, userId);
        validateOrderStatusPending(order);

        order.rejectOrder();

        return OrderResponse.of(order.getId(),order.getStore().getId(), order.getOrderStatus());
    }


    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = findByIdOrElseThrow(orderId);

        validateOrderStatusPending(order);
        validateOrderOwner(order, userId);


        order.cancelOrder();

        return OrderResponse.of(order.getId(),order.getStore().getId(), order.getOrderStatus());

    }


    public OrderDetailResponse getOrder(Long orderId) {
        Order order = orderRepository.findByIdWithStoreWithOrderItems(orderId).orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));

        return OrderDetailResponse.from(order);
    }

    private void convertAndAddOrderItems(Order order, Map<Long, Menu> menuMap, List<CartItem> items) {

        for (CartItem item : items) {
            Long itemId = item.getItemId();
            int quantity = item.getQuantity();

            Menu menu = menuMap.get(itemId);
            if (menu == null) {
                throw new BadRequestException("menu not found: " + itemId);
            }

            BigDecimal menuTotalPrice = menu.getPrice().multiply(BigDecimal.valueOf(quantity));
            OrderItem orderItem = OrderItem.createOrderItem(menu, menuTotalPrice, quantity);
            order.addOrderItem(orderItem);
        }
    }

    private BigDecimal calculateTotalPrice(Map<Long, Menu> menuMap, List<CartItem> items) {
        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartItem item : items) {
            Long itemId = item.getItemId();
            int quantity = item.getQuantity();

            Menu menu = menuMap.get(itemId);

            if (menu == null) {
                throw new NotFoundException("menu not found: " + itemId);
            }

            BigDecimal menuTotalPrice = menu.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(menuTotalPrice);
        }
        return totalPrice;

    }

    private void validateMinOrderAmount(Store store, BigDecimal totalPrice) {
        if (totalPrice.compareTo(store.getMinOrderPrice()) < 0) {
            throw new ForbiddenException(MIN_ORDER_AMOUNT_REQUIRED);
        }
    }

    private void validateOrderAvailability(Store store) {
        if (!store.isOrderAvailable()) {
            throw new ForbiddenException(ORDER_NOT_AVAILABLE);
        }
    }

    private void handlePoints(Order order, User user) {
        if (order.getUsedPoints() > 0) {
            usePoints(order, user);
        }else{
            earnPoints(order, user);
        }
    }

    private void earnPoints(Order order, User user) {
        int calculateEarnedPoints = pointPolicy.calculateEarnedPoints(order.getTotalPrice());
        user.addPoints(calculateEarnedPoints);
        savePointHistory(user, PointType.EARN, calculateEarnedPoints, order);
    }


    private void usePoints(Order order, User user) {
        user.usePoints(order.getUsedPoints());
        savePointHistory(user, PointType.USE, order.getUsedPoints(), order);
    }

    private void savePointHistory(User user, PointType earn, int calculateEarnedPoints, Order order) {
        pointHistoryRepository.save(new PointHistory(user, earn, calculateEarnedPoints, order.getId()));
    }

    private static void validateOrderStatusPending(Order order) {
        if (!order.isPending()) {
            throw new ForbiddenException(ORDER_STATUS_NOT_PENDING);
        }
    }

    private Order findByIdOrElseThrow(Long orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new NotFoundException(ORDER_NOT_FOUND));
    }

    private void validateStoreOwner(Order order, Long userId) {

        Store store = order.getStore();
        if (order.getStore() == null || !store.isOwner(userId)) {
            throw new ForbiddenException("해당 주문에 대한 권한이 없습니다.");
        }
    }

    private void validateOrderOwner(Order order, Long userId) {
        if (!order.isOwner(userId)) {
            throw new ForbiddenException(ORDER_NOT_OWNER);
        }
    }
}
