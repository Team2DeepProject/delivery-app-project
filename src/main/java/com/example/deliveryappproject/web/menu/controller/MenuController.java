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
//    @GetMapping
//    public ResponseEntity<List<MenuResponse>> findAll(){
//        return ResponseEntity.ok(menuService.findAll());
//    }

    //단건 메뉴 조회
//    @GetMapping("{/menuId}")
//    public ResponseEntity<MenuResponse> findByMenuId(@PathVariable Long menuId){
//        return ResponseEntity.ok(menuService.findByMenuId(menuId));
//    }



}
