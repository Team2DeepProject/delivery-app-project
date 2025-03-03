package com.example.deliveryappproject.domain.store.dto.response;

import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.entity.StoreState;
import com.example.deliveryappproject.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

import static jakarta.persistence.EnumType.STRING;

@Getter
@AllArgsConstructor
public class StoreGetAllResponse {

    private final Long id;
    private final String userName;
    private final String storeName;
    private final String storeState;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime openAt;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime closeAt;

    private final int minOrderPrice;

    public StoreGetAllResponse(Store store) {
        this.id = store.getId();
        this.userName = store.getUser().getUserName();
        this.storeName = store.getStoreName();
        this.storeState = store.getStoreState().name();
        this.openAt = store.getOpenAt();
        this.closeAt = store.getCloseAt();
        this.minOrderPrice = store.getMinOrderPrice().intValue();
    }

}
