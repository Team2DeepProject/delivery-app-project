package com.example.deliveryappproject.domain.store.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import com.example.deliveryappproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalTime;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "stores")
public class Store extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String storeName;

    @Enumerated(STRING)
    private StoreState storeState;

    private LocalTime openAt;

    private LocalTime closeAt;

    private BigDecimal minOrderPrice;

    @Builder
    public Store(User user, String storeName, LocalTime openAt, LocalTime closeAt, BigDecimal minOrderPrice) {
        this.user = user;
        this.storeName = storeName;
        this.storeState = StoreState.ACTIVE;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minOrderPrice = minOrderPrice;
    }

    public void updateStoreState(StoreState storeState) {
        this.storeState = storeState;
    }

    public void updateStore(String storeName, LocalTime openAt, LocalTime closeAt, BigDecimal minOrderPrice) {
        this.storeName = storeName;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.minOrderPrice = minOrderPrice;
    }
}
