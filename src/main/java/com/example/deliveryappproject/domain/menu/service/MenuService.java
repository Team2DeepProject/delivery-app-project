package com.example.deliveryappproject.domain.menu.service;

import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.menu.dto.request.MenuRequest;
import com.example.deliveryappproject.domain.menu.dto.response.MenuResponse;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.enums.MenuState;
import com.example.deliveryappproject.domain.menu.repository.MenuRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;

    //메뉴 생성
    @Transactional
    public void saveMenu(Long id, Long storeId, MenuRequest dto) {
        userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Not Found UserId"));

        if (!storeRepository.existsById(storeId))
            throw new NotFoundException("Not Found storeId");

        if (menuRepository.existsByMenuNameAndStoreId(dto.getMenuName(), storeId)) {
            throw new BadRequestException("동일메뉴는 불가능합니다.");
        }

        Optional<Store> store = storeRepository.findById(storeId);

        Menu menu = new Menu(dto.getMenuName(), dto.getPrice(), dto.getInformation(), store.get());

        menuRepository.save(menu);

    }

    //전체 메뉴 조회
    @Transactional(readOnly = true)
    public Page<MenuResponse> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Menu> menu = menuRepository.findAll(pageable);
        Page<MenuResponse> menuList = menu.map(m ->
                new MenuResponse(m.getId(),
                        m.getMenuName(),
                        m.getPrice(),
                        m.getInformation(),
                        m.getMenuState().toString(),
                        m.getStore().getStoreName()));

        return menuList;
    }

    //단건 메뉴 조회
    @Transactional(readOnly = true)
    public MenuResponse findByMenuId(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new NotFoundException("Not Found menuId"));

        return new MenuResponse(menu.getId(),
                menu.getMenuName(),
                menu.getPrice(),
                menu.getInformation(),
                menu.getMenuState().toString(),
                menu.getStore().getStoreName());
    }

    //가게별 메뉴 조회
    @Transactional(readOnly = true)
    public Page<MenuResponse> findByStoreId(int page, int size, Long storeId) {
        if (!storeRepository.existsById(storeId))
            throw new NotFoundException("Not Found storeId");

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Menu> menu = menuRepository.findByStoreIdAndMenuState(pageable, storeId, MenuState.SALE);

        Page<MenuResponse> menus = menu.map(m ->
                new MenuResponse(m.getId(),
                        m.getMenuName(),
                        m.getPrice(),
                        m.getInformation(),
                        m.getMenuState().toString(),
                        m.getStore().getStoreName()));

        return menus;
    }

    //메뉴수정
    @Transactional
    public void updateMenu(Long userId, Long menuId, MenuRequest dto) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not Found userId"));

        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new NotFoundException("Not Found menuId"));

        menu.update(dto.getMenuName(),
                dto.getPrice(),
                dto.getInformation());

    }

    //메뉴 삭제(상태 변경만)
    @Transactional
    public void deleteMenu(Long userId, Long menuId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("Not Found userId"));

        Menu menu = menuRepository.findById(menuId).orElseThrow(
                () -> new NotFoundException("Not Found MenuId"));

        //삭제할 메뉴의 가게와 로그인한 유저가 소유한 가게가 맞으면 삭제
        Store menuStore = menu.getStore();
        List<Store> stores = storeRepository.findByUserId(userId);

        for (Store userStore : stores) {
            if (ObjectUtils.nullSafeEquals(userStore, menuStore))//  객체 비교에는 equals
                menu.setMenuState(MenuState.DELETE);
        }

        if(menu.getMenuState()==MenuState.SALE)
            throw new BadRequestException("소유한 가게를 입력해주세요.");
    }
}
