package com.example.deliveryappproject.domain.menu.service;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.menu.dto.MenuRequest;
import com.example.deliveryappproject.domain.menu.dto.MenuResponse;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.entity.MenuState;
import com.example.deliveryappproject.domain.menu.repository.MenuRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.entity.StoreState;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.entity.UserRole;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
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
    public MenuResponse saveMenu(Long id, UserRole userRole, MenuRequest dto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Not Found UserId"));

        if (UserRole.OWNER != userRole) {
            throw new BadRequestException("사장님만 메뉴를 생성할 수 있습니다.");
        }

        if (menuRepository.existsByMenuNameAndStoreId(dto.getMenuName(),dto.getStoreId())){
            throw new BadRequestException("동일메뉴는 불가능합니다.");
        }

        Optional<Store>  store=  storeRepository.findById(dto.getStoreId());

        Menu menu = new Menu(dto.getMenuName(), dto.getPrice(), dto.getInformation(), store.get());

        Menu savedMenu = menuRepository.save(menu);

        return new MenuResponse(savedMenu.getId(),
                savedMenu.getMenuName(),
                savedMenu.getPrice(),
                savedMenu.getInformation(),
                MenuState.SALE.toString(),
                savedMenu.getStore().getStoreName()
        );
    }

    //전체 메뉴 조회
    @Transactional(readOnly = true)
    public List<MenuResponse> findAll() {
        List<Menu> menu = menuRepository.findAll();
        List<MenuResponse> menuList = new ArrayList<>();

        for (Menu m : menu) {
            menuList.add(new MenuResponse(m.getId(),
                    m.getMenuName(),
                    m.getPrice(),
                    m.getInformation(),
                    m.getMenuState().toString(),
                    m.getStore().getStoreName()));
        }
        return menuList;
    }

    //단건 메뉴 조회
    @Transactional(readOnly = true)
    public MenuResponse findByMenuId(Long menuId) {
        Menu menu=menuRepository.findById(menuId).orElseThrow(
                () -> new BadRequestException("Not Found menuId"));

        return new MenuResponse(menu.getId(),
                menu.getMenuName(),
                menu.getPrice(),
                menu.getInformation(),
                menu.getMenuState().toString(),
                menu.getStore().getStoreName());
    }

    //가게별 메뉴 조회
    @Transactional(readOnly = true)
    public List<MenuResponse> findByStoreId(Long storeId){
        if(!storeRepository.existsById(storeId))
                throw new BadRequestException("Not Found store");

        List<Menu> menu= menuRepository.findByStoreIdAndMenuState(storeId, MenuState.SALE);

        List<MenuResponse> menus= new ArrayList<>();

        for(Menu m : menu){
            menus.add(new MenuResponse( m.getId(),
                    m.getMenuName(),
                    m.getPrice(),
                    m.getInformation(),
                    m.getMenuState().toString(),
                    m.getStore().getStoreName()));
        }
        return menus;
    }

    //메뉴수정
    @Transactional
    public MenuResponse updateMenu(Long userId, UserRole userRole, Long menuId, MenuRequest dto){
        User user=userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not Found userId"));

        if (UserRole.OWNER != userRole) {
            throw new BadRequestException("사장님만 메뉴를 수정할 수 있습니다.");
        }
        Menu menu=menuRepository.findById(menuId).orElseThrow(
                () -> new BadRequestException("Not Found menu"));

        menu.update(dto.getMenuName(),
                dto.getPrice(),
                dto.getInformation());

        return new MenuResponse(menu.getId(),
                menu.getMenuName(),
                menu.getPrice(),
                menu.getInformation(),
                menu.getMenuState().toString(),
                menu.getStore().getStoreName());
    }

    //메뉴 삭제(상태 변경만)
    @Transactional
    public void deleteMenu(Long userId, UserRole userRole, Long menuId){
        User user=userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException("Not Found userId"));

        if (UserRole.OWNER != userRole) {
            throw new BadRequestException("사장님만 메뉴를 삭제할 수 있습니다.");
        }

        Menu menu=menuRepository.findById(menuId).orElseThrow(
                () -> new BadRequestException("Not Found Menu"));

        //삭제할 메뉴의 가게와 로그인한 유저가 소유한 가게가 맞으면 삭제
        Store menuStore= menu.getStore();
        List<Store> stores= storeRepository.findByUserId(userId);

        for(Store userStore: stores){
            if(userStore==menuStore)
                menu.setMenuState(MenuState.DELETE);
        }
    }
}
