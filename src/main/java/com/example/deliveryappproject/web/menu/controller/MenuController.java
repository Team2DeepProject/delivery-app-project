package com.example.deliveryappproject.web.menu.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.response.MessageResponse;
import com.example.deliveryappproject.domain.menu.dto.request.MenuRequest;
import com.example.deliveryappproject.domain.menu.dto.response.MenuResponse;
import com.example.deliveryappproject.domain.menu.service.MenuService;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    //메뉴 생성
    @AuthPermission(role = UserRole.OWNER)
    @PostMapping("/stores/{storeId}/menus")
    public MessageResponse saveMenu(@Auth AuthUser authUser,
                                    @PathVariable Long storeId,
                                    @RequestBody MenuRequest dto) {
        menuService.saveMenu(authUser.getId(), storeId, dto);
        return MessageResponse.of("메뉴를 추가했습니다.");
    }

    //전체 메뉴 조회
    @GetMapping("/menus")
    public ResponseEntity<Page<MenuResponse>> findAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(menuService.findAll(page, size));
    }

    //단건 메뉴 조회
    @GetMapping("/menus/{menuId}")
    public ResponseEntity<MenuResponse> findByMenuId(@PathVariable Long menuId) {
        return ResponseEntity.ok(menuService.findByMenuId(menuId));
    }

    //가게별 메뉴 조회
    @GetMapping("/store/{storeId}/menus")
    public ResponseEntity<Page<MenuResponse>> findByStoreId(@PathVariable Long storeId,
                                                            @RequestParam(defaultValue = "1") int page,
                                                            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(menuService.findByStoreId(page, size, storeId));
    }

    //메뉴 수정
    @AuthPermission(role = UserRole.OWNER)
    @PatchMapping("/menus/{menuId}")
    public MessageResponse updateMenu(@Auth AuthUser authUser, @PathVariable Long menuId, @RequestBody MenuRequest dto) {
        menuService.updateMenu(authUser.getId(), menuId, dto);
        return MessageResponse.of("메뉴를 수정했습니다.");
    }

    //메뉴 삭제
    @AuthPermission(role = UserRole.OWNER)
    @DeleteMapping("/menus/{menuId}")
    public MessageResponse deleteMenu(@Auth AuthUser authUser, @PathVariable Long menuId) {
        menuService.deleteMenu(authUser.getId(), menuId);
        return MessageResponse.of("해당 메뉴를 삭제했습니다.");
    }
}
