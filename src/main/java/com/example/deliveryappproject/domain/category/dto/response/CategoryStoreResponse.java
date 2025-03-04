package com.example.deliveryappproject.domain.category.dto.response;

import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.store.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class CategoryStoreResponse {

    private final String categoryName;
    private final List<StoreGetAllResponse> categoryStores;

    public static CategoryStoreResponse fromDto(String categoryName, List<StoreGetAllResponse> categoryStores) {
        return CategoryStoreResponse.builder()
                .categoryName(categoryName)
                .categoryStores(categoryStores)
                .build();
    }
}
