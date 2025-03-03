package com.example.deliveryappproject.domain.menu.service;

import com.example.deliveryappproject.common.annotation.Auth;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.menu.dto.MenuRequest;
import com.example.deliveryappproject.domain.menu.dto.MenuResponse;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.repository.MenuRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.entity.UserRole;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    @Transactional
    public MenuResponse saveMenu(Long id, UserRole userRole, MenuRequest dto) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Not Found UserId"));

        if (UserRole.OWNER != userRole) {
            throw new BadRequestException("사장님만 메뉴를 생성할 수 있습니다.");
        }

        Menu menu = new Menu(dto.getMenuName(), dto.getPrice(), dto.getInformation());

        Menu savedMenu = menuRepository.save(menu);

        return new MenuResponse(savedMenu.getId(),
                savedMenu.getMenuName(),
                savedMenu.getPrice(),
                savedMenu.getInformation());
    }

    @Transactional(readOnly = true)
    public List<MenuResponse> findAll() {
        List<Menu> menu = menuRepository.findAll();
        List<MenuResponse> menuList = new ArrayList<>();

        for (Menu m : menu) {
            menuList.add(new MenuResponse(m.getId(),
                    m.getMenuName(),
                    m.getPrice(),
                    m.getInformation()));
        }
        return menuList;
    }

    @Transactional(readOnly = true)
    public MenuResponse findByMenuId(Long menuId) {
        Menu menu=menuRepository.findById(menuId).orElseThrow(
                () -> new BadRequestException("Not Found menuId"));

        return new MenuResponse(menu.getId(),
                menu.getMenuName(),
                menu.getPrice(),
                menu.getInformation());
    }


}
