package com.example.deliveryappproject.domain.store.dto.response;

import com.example.deliveryappproject.domain.store.entity.Store;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class StoreGetResponse<T> {

    private final String storeName;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime openAt;

    @JsonFormat(pattern = "HH:mm")
    private final LocalTime closeAt;

    private final int bookmarkCount;

    private final int minOrderPrice;

    private final T menu;

    public static <T> StoreGetResponse<T> fromDto(Store store, int bookmarkCount, T menu) {
        return StoreGetResponse.<T>builder()
                .storeName(store.getStoreName())
                .openAt(store.getOpenAt())
                .closeAt(store.getCloseAt())
                .bookmarkCount(bookmarkCount)
                .minOrderPrice(store.getMinOrderPrice().intValue())
                .menu(menu)
                .build();
    }
}
