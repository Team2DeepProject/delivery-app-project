package com.example.deliveryappproject.domain.store.dto.response;

import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.entity.StoreState;
import com.example.deliveryappproject.domain.user.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalTime;

import static jakarta.persistence.EnumType.STRING;

@Getter
@Builder
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

    public static StoreGetAllResponse fromDto(Store store) {
        return StoreGetAllResponse.builder()
                .id(store.getId())
                .userName(store.getStoreName())
                .storeName(store.getStoreName())
                .storeState(store.getStoreState().name())
                .openAt(store.getOpenAt())
                .closeAt(store.getCloseAt())
                .minOrderPrice(store.getMinOrderPrice().intValue())
                .build();
    }

}
