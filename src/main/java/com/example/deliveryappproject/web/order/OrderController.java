package com.example.deliveryappproject.web.order;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.order.dto.OrderRequest;
import com.example.deliveryappproject.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<String> order(@Auth AuthUser authUser, @RequestBody OrderRequest orderRequest) {
        orderService.order(authUser.getId(), orderRequest);


        return ResponseEntity.ok("주문 요청이 완료되었습니다. 가게에서 주문을 확인 중입니다.");
    }

    @GetMapping("/{orderId}")

    @PatchMapping("/{orderId}/accept")
    public ResponseEntity<String> acceptOrder(@Auth AuthUser authUser, @PathVariable Long orderId) {
        orderService.acceptOrder(authUser.getId(),orderId);


        return ResponseEntity.ok("주문이 수락되었습니다");
    }

    @PatchMapping("/{orderId}/reject")
    public ResponseEntity<String> rejectOrder(@PathVariable Long orderId) {
        orderService.rejectOrder(orderId);

        return ResponseEntity.ok("주문이 거절되었습니다");
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(@Auth AuthUser authUser, @PathVariable Long orderId) {
        orderService.cancelOrder(authUser.getId(), orderId);

        return ResponseEntity.ok("주문이 취소되었습니다");
    }
}
