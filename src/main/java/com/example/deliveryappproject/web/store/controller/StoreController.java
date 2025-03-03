package com.example.deliveryappproject.web.store.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.store.dto.request.StoreCreateRequest;
import com.example.deliveryappproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public void createStore(
            @Auth AuthUser authUser,
            @Valid @RequestBody StoreCreateRequest storeCreateRequest
    ) {
        storeService.createStore(authUser, storeCreateRequest);
    }

    @GetMapping
    public Page<StoreGetAllResponse> getAllStore(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return storeService.getAllStore(page, size);
    }

    /*
    TODO: 메뉴 구현 이후 가게 단건 구현 예정
     */

    @PatchMapping("/{storeId}")
    public void updateStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @RequestBody StoreUpdateRequest storeUpdateRequest
    ) {
        storeService.updateStore(authUser, storeId, storeUpdateRequest);
    }
}
