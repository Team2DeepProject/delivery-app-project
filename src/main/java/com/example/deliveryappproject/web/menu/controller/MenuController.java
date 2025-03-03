package com.example.deliveryappproject.web.menu.controller;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.domain.menu.dto.MenuRequest;
import com.example.deliveryappproject.domain.menu.dto.MenuResponse;
import com.example.deliveryappproject.domain.menu.service.MenuService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
public class MenuController {

    private final MenuService menuService;

    //메뉴 생성
    @PostMapping
    public ResponseEntity<MenuResponse> saveMenu(@Auth AuthUser authUser ,
                                                 @RequestBody MenuRequest dto){
        return ResponseEntity.ok(menuService.saveMenu(authUser.getId(), authUser.getUserRole(), dto));
    }

    //전체 메뉴 조회
    @GetMapping
    public ResponseEntity<List<MenuResponse>> findAll(){
        return ResponseEntity.ok(menuService.findAll());
    }

    //단건 메뉴 조회
    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponse> findByMenuId(@PathVariable Long menuId){
        return ResponseEntity.ok(menuService.findByMenuId(menuId));
    }

    //가게별 메뉴 조회
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<MenuResponse>> findByStoreId(@PathVariable Long storeId){
        return ResponseEntity.ok(menuService.findByStoreId(storeId));
    }

    //메뉴 수정
    @PatchMapping("/{menuId}")
    public ResponseEntity<MenuResponse> updateMenu(@Auth AuthUser authUser ,@PathVariable Long menuId, @RequestBody MenuRequest dto){
        return ResponseEntity.ok(menuService.updateMenu(authUser.getId(), authUser.getUserRole(), menuId, dto));
    }

    //메뉴 삭제
    @DeleteMapping("/{menuId}")
    public ResponseEntity<String> deleteMenu(@Auth AuthUser authUser ,@PathVariable Long menuId){
        menuService.deleteMenu(authUser.getId(), authUser.getUserRole(), menuId);
        return ResponseEntity.ok("해당 메뉴를 삭제했습니다.");
    }
}
