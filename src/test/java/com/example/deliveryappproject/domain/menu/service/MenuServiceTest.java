package com.example.deliveryappproject.domain.menu.service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.menu.dto.MenuRequest;
import com.example.deliveryappproject.domain.menu.dto.MenuResponse;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.repository.MenuRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.entity.StoreState;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import org.apache.catalina.Manager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class MenuServiceTest {

    @Mock
    private MenuRepository menuRepository;

    @Mock
    private StoreRepository storeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MenuService menuService;

    @Test
    void 메뉴_생성_테스트(){

        //given
        long userId= 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time= LocalTime.of(9,30,30);

        MenuRequest request = new MenuRequest ("menuName", price, "information", 1L);
        AuthUser authUser = new AuthUser(1L, "email@gmail.com", UserRole.OWNER);
        UserRole userRole = authUser.getUserRole();

        User user= new User("email@gmail.com", "123!@#abcde","userName",userRole);
        Store store= new Store(user, "storeName",  time, time ,price);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        Menu menu = new Menu("menuName", price, "information", store);

        given(menuRepository.save(any())).willReturn(menu);

        // when
        MenuResponse result = menuService.saveMenu(userId, request);

        // then
        assertNotNull(result);
    }

//    @Test
//    void OWNER_아닌_유저가_메뉴를_생성할_경우_에러가_발생한다(){
//
//    }

    @Test
    void 동일한_메뉴를_추가하면_에러가_발생한다(){
        // given
        long userId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        User user= new User("email@gmail.com", "123!@#abcde","userName",UserRole.OWNER);
        MenuRequest request = new MenuRequest ("menuName", price, "information", 1L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        given(menuRepository.existsByMenuNameAndStoreId(request.getMenuName(), request.getStoreId())).willReturn(true);

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            menuService.saveMenu(userId, request);
        });

        // then
        assertEquals("동일메뉴는 불가능합니다.", exception.getMessage());
    }

    @Test
    void 메뉴_전체조회_테스트(){
        // given
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time= LocalTime.of(9,30,30);
        Pageable pageable = PageRequest.of(0, 10);

        User user= new User("email@gmail.com", "123!@#abcde","userName",UserRole.OWNER);
        Store store= new Store(user, "storeName",  time, time ,price);

        List<Menu> menuList = IntStream.rangeClosed(1, 20).mapToObj(i ->
                new Menu("menuName",
                        price,
                        "information",
                        store)).collect(Collectors.toList());

        Page<Menu> page = new PageImpl<>(menuList.subList(0, 10), pageable, menuList.size());

        given(menuRepository.findAll(pageable)).willReturn(page);

        // when
        Page<MenuResponse> menuResponses = menuService.findAll(1,10);

        // then
        assertEquals(2, menuResponses.getTotalPages());
        assertEquals(20, menuResponses.getTotalElements());
        assertEquals(0, menuResponses.getNumber());
        assertEquals(10 , menuResponses.getSize());
        assertTrue(menuResponses.hasNext());
    }

    @Test
    void 메뉴_단건조회_테스트(){
        //given
        Long menuId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time= LocalTime.of(9,30,30);
        User user= new User("email@gmail.com", "123!@#abcde","userName",UserRole.OWNER);
        Store store= new Store(user, "storeName",  time, time ,price);

        Menu mockMenu = new Menu("menuName", price, "information", store);

        given(menuRepository.findById(menuId)).willReturn(Optional.of(mockMenu));

        //when
        MenuResponse menuResponse = menuService.findByMenuId(menuId);

        //then
        assertEquals("menuName",menuResponse.getMenuName());
    }

    @Test
    void 메뉴_가게별조회_테스트(){
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time= LocalTime.of(9,30,30);
        Pageable pageable = PageRequest.of(0, 10);

        User user= new User("email@gmail.com", "123!@#abcde","userName",UserRole.OWNER);
        Store store= new Store(user, "storeName",  time, time ,price);

        List<Menu> menuList = IntStream.rangeClosed(1, 20).mapToObj(i ->
                new Menu("menuName",
                        price,
                        "information",

                        store)).collect(Collectors.toList());

        Page<Menu> page = new PageImpl<>(menuList.subList(0, 10), pageable, menuList.size());

        given(menuRepository.findAll(pageable)).willReturn(page);

        // when
        Page<MenuResponse> menuResponses = menuService.findAll(1,10);

        // then
        assertEquals(2, menuResponses.getTotalPages());
        assertEquals(20, menuResponses.getTotalElements());
        assertEquals(0, menuResponses.getNumber());
        assertEquals(10 , menuResponses.getSize());
        assertTrue(menuResponses.hasNext());

    }

    @Test
    void 메뉴_가게별조회시_삭제된_menu는_조회하지_않는다(){


    }

    @Test
    void 메뉴_수정_테스트(){

    }

    @Test
    void 메뉴_삭제_테스트(){

    }

    @Test
    void OWNER의_가게의_메뉴만_삭제할_수_있다(){

    }
}
