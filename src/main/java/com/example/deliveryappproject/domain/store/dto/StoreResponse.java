package com.example.deliveryappproject.domain.store.dto;

import lombok.Getter;

@Getter
public class StoreResponse {

    private final Long storeId;
    private final String storeName;

    private StoreResponse(Long storeId, String storeName) {
        this.storeId = storeId;
        this.storeName = storeName;
    }

    public static StoreResponse of(Long storeId, String storeName) {
        return new StoreResponse(storeId, storeName);
    }
}
