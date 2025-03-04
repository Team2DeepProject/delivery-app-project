package com.example.deliveryappproject.domain.order.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.cart.model.CartItem;
import com.example.deliveryappproject.domain.cart.repository.CartRepository;
import com.example.deliveryappproject.domain.delivery.entity.Delivery;
import com.example.deliveryappproject.domain.delivery.repository.DeliveryRepository;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.repository.MenuRepository;
import com.example.deliveryappproject.domain.order.dto.OrderRequest;
import com.example.deliveryappproject.domain.order.entity.Order;
import com.example.deliveryappproject.domain.order.entity.OrderItem;
import com.example.deliveryappproject.domain.order.entity.OrderStatus;
import com.example.deliveryappproject.domain.order.repository.OrderRepository;
import com.example.deliveryappproject.domain.order.service.dto.OrderResponse;
import com.example.deliveryappproject.domain.policy.PointPolicy;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import com.example.deliveryappproject.domain.user.userpoint.PointHistoryRepository;
import com.example.deliveryappproject.domain.user.userpoint.entity.PointHistory;
import com.example.deliveryappproject.domain.user.userpoint.entity.PointType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartRepository cartRepository;
    private final StoreRepository storeRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final PointPolicy pointPolicy;
    private final DeliveryRepository deliveryRepository;
    private final PointHistoryRepository pointHistoryRepository;


    @Transactional
    public OrderResponse order(Long userId, OrderRequest orderRequest) {

        Long storeId = cartRepository.findStoreId(userId);

        if (storeId == null) {
            throw new BadRequestException("Cart is empty");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new BadRequestException("store Not Found"));

//        if (!store.isOrderAvailable()) {
//            throw new BadRequestException("주문 가능한 시간이 아닙니다.");
//        }

        //최소 주문 금액 검증
        List<CartItem> items = cartRepository.findItems(userId);
        List<Long> itemIds = items.stream()
                .map(item -> item.getItemId())
                .toList();

        //임시
        List<Menu> menus = menuRepository.findAllById(itemIds);
        Map<Long, Menu> menuMap = menus.stream().collect(Collectors.toMap(Menu::getId, menu -> menu));
        BigDecimal totalPrice = calculateTotalPrice(menuMap, items);

        if (totalPrice.compareTo(store.getMinOrderPrice()) < 0) {
            throw new BadRequestException("최소주문 금액을 맞춰주세요");
        }

        //포인트 사용 검증
        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));
        int usePoints = orderRequest.getUsePoints();

        if (usePoints > 0 && usePoints > user.getPoint()) {
            throw new BadRequestException("포인트가 부족합니다.");
        }
        Order order = new Order(user, store, usePoints);

        convertAndAddOrderItems(order,menuMap, items);


        orderRepository.save(order);

        //장바구니 비우기
        cartRepository.clear(userId);


        return OrderResponse.of(order.getId(),storeId, order.getOrderStatus());

        // ✅ 최소 주문 금액 검증

    }


    @Transactional
    public OrderResponse acceptOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithOrderItems(orderId).orElseThrow(() -> new BadRequestException("order not found"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("orderStatus is not pending");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new BadRequestException("User not found"));

        if (order.getUsedPoints() > 0) {
            if(user.getPoint() < order.getUsedPoints()){
                throw new BadRequestException("포인트가 부족합니다.");
            }
            user.usePoints(order.getUsedPoints());
            pointHistoryRepository.save(new PointHistory(user, PointType.USE, order.getUsedPoints(),order.getId()));

        }else{
            int calculateEarnedPoints = pointPolicy.calculateEarnedPoints(order.getTotalPrice());
            user.addPoints(calculateEarnedPoints);
            pointHistoryRepository.save(new PointHistory(user, PointType.EARN, calculateEarnedPoints,order.getId()));

        }
        order.acceptOrder();

        Delivery delivery = new Delivery(order);
        deliveryRepository.save(delivery);

        return OrderResponse.of(order.getId(),order.getStore().getId(), order.getOrderStatus());

    }

    @Transactional
    public OrderResponse rejectOrder(Long orderId) {
        Order order = orderRepository.findByIdWithOrderItems(orderId).orElseThrow(() -> new BadRequestException("order not found"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("orderStatus is not pending");
        }

        order.rejectOrder();

        return OrderResponse.of(order.getId(),order.getStore().getId(), order.getOrderStatus());
    }

    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findByIdWithOrderItems(orderId).orElseThrow(() -> new BadRequestException("order not found"));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BadRequestException("orderStatus is not pending");
        }

        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new BadRequestException("주문자가 아닙니다.");
        }

        order.cancelOrder();

        return OrderResponse.of(order.getId(),order.getStore().getId(), order.getOrderStatus());


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
                throw new BadRequestException("menu not found: " + itemId);
            }

            BigDecimal menuTotalPrice = menu.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(menuTotalPrice);
        }
        return totalPrice;

    }



}
