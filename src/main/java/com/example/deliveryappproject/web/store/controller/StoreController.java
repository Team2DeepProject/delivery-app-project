package com.example.deliveryappproject.web.store.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.Response;
import com.example.deliveryappproject.domain.menu.dto.MenuResponse;
import com.example.deliveryappproject.domain.store.dto.request.StoreCreateRequest;
import com.example.deliveryappproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetResponse;
import com.example.deliveryappproject.domain.store.service.StoreService;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    /* 가게 작성 */
    @AuthPermission(role = UserRole.OWNER)
    @PostMapping
    public Response<Void> createStore(
            @Auth AuthUser authUser,
            @Valid @RequestBody StoreCreateRequest storeCreateRequest
    ) {
        storeService.createStore(authUser, storeCreateRequest);
        return Response.empty();
    }

    /* 가게 다건 조회 */
    @GetMapping
    public Response<StoreGetAllResponse> getAllStore(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.fromPage(storeService.getAllStore(page, size));
    }

    /* 가게 단건 조회 */
    @GetMapping("/{storeId}")
    public Response<StoreGetResponse<Page<MenuResponse>>> getStore(
            @PathVariable Long storeId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return Response.of(storeService.getStore(storeId, page, size));
    }

    @AuthPermission(role = UserRole.OWNER)
    @PatchMapping("/{storeId}")
    public Response<Void> updateStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId,
            @RequestBody StoreUpdateRequest storeUpdateRequest
    ) {
        storeService.updateStore(authUser, storeId, storeUpdateRequest);
        return Response.empty();
    }

    @AuthPermission(role = UserRole.OWNER)
    @DeleteMapping("/{storeId}")
    public Response<Void> deleteStore(
            @Auth AuthUser authUser,
            @PathVariable Long storeId
    ) {
        storeService.deleteStore(authUser, storeId);
        return Response.empty();
    }
}
