package com.example.deliveryappproject.web.delivery;

import com.example.deliveryappproject.domain.delivery.service.DeliveryService;
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

    @PatchMapping("/{deliveryId}/start")
    public ResponseEntity<Void> startDelivery(@PathVariable Long deliveryId) {
        deliveryService.startDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{deliveryId}/complete")
    public ResponseEntity<Void> completeDelivery(@PathVariable Long deliveryId) {
        deliveryService.completeDelivery(deliveryId);
        return ResponseEntity.noContent().build();
    }
}
