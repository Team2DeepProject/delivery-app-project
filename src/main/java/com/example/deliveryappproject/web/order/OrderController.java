package com.example.deliveryappproject.web.order;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.Response;
import com.example.deliveryappproject.config.aop.annotation.OrderLogging;
import com.example.deliveryappproject.domain.order.dto.OrderDetailResponse;
import com.example.deliveryappproject.domain.order.dto.OrderRequest;
import com.example.deliveryappproject.domain.order.service.OrderService;
import com.example.deliveryappproject.domain.order.service.dto.OrderResponse;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
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

    @OrderLogging
    @PostMapping
    public Response<OrderResponse> order(@Auth AuthUser authUser, @RequestBody OrderRequest orderRequest) {

        return Response.of(orderService.order(authUser.getId(), orderRequest));
    }

    @GetMapping("/{orderId}")
    public Response<OrderDetailResponse> getOrder(@PathVariable Long orderId) {
        return Response.of(orderService.getOrder(orderId));
    }

    @OrderLogging
    @AuthPermission(role = UserRole.OWNER)
    @PatchMapping("/{orderId}/accept")
    public Response<OrderResponse> acceptOrder(@Auth AuthUser authUser, @PathVariable Long orderId) {
        return Response.of(orderService.acceptOrder(authUser.getId(), orderId));
    }

    @OrderLogging
    @AuthPermission(role = UserRole.OWNER)
    @PatchMapping("/{orderId}/reject")
    public Response<OrderResponse> rejectOrder(@Auth AuthUser authUser, @PathVariable Long orderId) {

        return Response.of(orderService.rejectOrder(authUser.getId(), orderId));
    }

    @OrderLogging
    @PatchMapping("/{orderId}/cancel")
    public Response<OrderResponse> cancelOrder(@Auth AuthUser authUser, @PathVariable Long orderId) {

        return Response.of(orderService.cancelOrder(authUser.getId(), orderId));
    }
}
