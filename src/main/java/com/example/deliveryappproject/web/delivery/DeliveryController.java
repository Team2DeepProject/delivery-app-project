package com.example.deliveryappproject.web.delivery;

import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.response.Response;
import com.example.deliveryappproject.config.aop.annotation.OrderLogging;
import com.example.deliveryappproject.domain.delivery.dto.DeliveryResponse;
import com.example.deliveryappproject.domain.delivery.service.DeliveryService;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @AuthPermission(role = UserRole.RIDER)
    @PatchMapping("/{deliveryId}/start")
    public Response<Void> startDelivery(@PathVariable Long deliveryId) {
        deliveryService.startDelivery(deliveryId);
        return Response.empty();
    }

    @AuthPermission(role = UserRole.RIDER)
    @OrderLogging
    @PatchMapping("/{deliveryId}/complete")
    public Response<DeliveryResponse> completeDelivery(@PathVariable Long deliveryId) {
        return Response.of(deliveryService.completeDelivery(deliveryId));
    }
}
