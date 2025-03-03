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
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    public MenuResponse saveMenu(Long id, UserRole userRole , MenuRequest dto){
        User user =userRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Not Found UserId"));

        if(UserRole.OWNER!=userRole){
            throw new BadRequestException("사장님만 메뉴를 생성할 수 있습니다.");
        }

        Menu menu=new Menu(dto.getMenuName(), dto.getPrice(),dto.getInformation());

        Menu savedMenu = menuRepository.save(menu);

        return new MenuResponse(savedMenu.getId(),
                savedMenu.getMenuName(),
                savedMenu.getPrice(),
                savedMenu.getInformation());
    }

//    public List<MenuResponse> findAll() {
//        return new List;
//    }
//
//    public MenuResponse findByMenuId(Long menuId) {
//    }
}
