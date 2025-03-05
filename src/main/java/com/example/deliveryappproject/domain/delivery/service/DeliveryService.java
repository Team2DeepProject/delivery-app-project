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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public void startDelivery(Long userId, Long deliveryId) {
        Delivery delivery = findByIdOrElseThrow(deliveryId);

        Order order = delivery.getOrder();

        if (order.getOrderStatus() != OrderStatus.ACCEPT) {
            throw new BadRequestException("Order is not accept");
        }

        if (delivery.getDeliveryStatus() != DeliveryStatus.PENDING) {
            throw new BadRequestException("Delivery is not pending");
        }

        delivery.startDelivery(userId);
    }

    @Transactional
    public DeliveryResponse completeDelivery(Long userId, Long deliveryId) {

        Delivery delivery = findByIdOrElseThrow(deliveryId);
        if (delivery.getDeliveryStatus() != DeliveryStatus.READY) {
            throw new BadRequestException("Delivery is not ready");
        }

        if (!delivery.isAssignedTo(userId)) {
            throw new ForbiddenException("해당 배송을 맡은 유저가 아닙니다.");
        }


        delivery.completeDelivery();

        Order order = delivery.getOrder();

        order.completeOrder();

        return DeliveryResponse.of(order.getId(),order.getStore().getId(), order.getOrderStatus());

    }

    private Delivery findByIdOrElseThrow(Long deliveryId) {
        return deliveryRepository.findById(deliveryId).orElseThrow(() -> new NotFoundException("delivery not found"));
    }
}
