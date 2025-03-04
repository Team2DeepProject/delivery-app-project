package com.example.deliveryappproject.domain.delivery.entity;

import com.example.deliveryappproject.domain.order.entity.Order;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery")
    private Order order;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    public Delivery(Order order) {
        this.order = order;
        this.deliveryStatus = DeliveryStatus.PENDING;
    }

    public void startDelivery() {
        this.deliveryStatus = DeliveryStatus.READY;
    }

    public void completeDelivery() {

        this.deliveryStatus = DeliveryStatus.COMP;
    }
}
