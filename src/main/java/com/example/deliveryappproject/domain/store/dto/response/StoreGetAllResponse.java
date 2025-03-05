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

    private final String storeName;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime openAt;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime closeAt;

    private final int bookmarkCount;

    private final int minOrderPrice;

    public static StoreGetAllResponse fromDto(Store store, int bookmarkCount) {
        return StoreGetAllResponse.builder()
                .storeName(store.getStoreName())
                .openAt(store.getOpenAt())
                .closeAt(store.getCloseAt())
                .bookmarkCount(bookmarkCount)
                .minOrderPrice(store.getMinOrderPrice().intValue())
                .build();
    }

}
