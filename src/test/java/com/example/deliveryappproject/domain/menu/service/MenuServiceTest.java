package com.example.deliveryappproject.domain.menu.service;

import com.example.deliveryappproject.domain.menu.enums.MenuState;
import org.assertj.core.util.Lists;
import org.mockito.Mockito;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.menu.dto.request.MenuRequest;
import com.example.deliveryappproject.domain.menu.dto.response.MenuResponse;
import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.repository.MenuRepository;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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

    AuthUser authUser = new AuthUser(1L, "email@gmail.com", UserRole.OWNER);

    @Test
    void 메뉴_생성_테스트() {
        //given
        long userId = 1L;
        long storeId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time = LocalTime.of(9, 30, 30);

        MenuRequest request = new MenuRequest("menuName", price, "information");
        UserRole userRole = authUser.getUserRole();

        User user = new User("email@gmail.com", "123!@#abcde", "userName", userRole);
        Store store = new Store(user, "storeName", time, time, price);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        Menu menu = new Menu("menuName", price, "information", store);

        menuRepository.save(menu);

        // when
        menuService.saveMenu(userId, storeId, request);

        // then
        verify(menuRepository, times(1)).save(menu);
    }

//    @Test
//    void 메뉴_생성시_유저가_존재하지_않으면_에러가_발생한다(){
//
//    }

    @Test
    void 메뉴_생성시_동일한_메뉴를_추가하면_에러가_발생한다() {
        // given
        long userId = 1L;
        long storeId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        MenuRequest request = new MenuRequest("menuName", price, "information");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        given(menuRepository.existsByMenuNameAndStoreId(anyString(), anyLong())).willReturn(true);

        // when
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            menuService.saveMenu(userId, storeId, request);//any도 가능한지 검색
        });

        // then
        assertEquals("동일메뉴는 불가능합니다.", exception.getMessage());
    }

    @Test
    void 메뉴_전체조회_테스트() {
        // given
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time = LocalTime.of(9, 30, 30);
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        Store store = new Store(user, "storeName", time, time, price);

        List<Menu> menuList = IntStream.rangeClosed(1, 20).mapToObj(i ->
                new Menu("menuName",
                        price,
                        "information",
                        store)).collect(Collectors.toList());

        Page<Menu> page = new PageImpl<>(menuList.subList(0, 10), pageable, menuList.size());

        given(menuRepository.findAll(pageable)).willReturn(page);

        // when
        Page<MenuResponse> menuResponses = menuService.findAll(1, 10);

        // then
        assertEquals(2, menuResponses.getTotalPages());
        assertEquals(20, menuResponses.getTotalElements());
        assertEquals(0, menuResponses.getNumber());
        assertEquals(10, menuResponses.getSize());
        assertTrue(menuResponses.hasNext());
    }

    @Test
    void 메뉴_단건조회_테스트() {
        //given
        Long menuId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time = LocalTime.of(9, 30, 30);
        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        Store store = new Store(user, "storeName", time, time, price);

        Menu mockMenu = new Menu("menuName", price, "information", store);

        given(menuRepository.findById(menuId)).willReturn(Optional.of(mockMenu));

        //when
        MenuResponse menuResponse = menuService.findByMenuId(menuId);

        //then
        assertEquals("menuName", menuResponse.getMenuName());
    }

    @Test
    void 메뉴_가게별조회_테스트() {
        Long storeId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time = LocalTime.of(9, 30, 30);
        Pageable pageable = PageRequest.of(0, 10);

        User user = new User("email@gmail.com", "123!@#abcde", "userName", UserRole.OWNER);
        Store store = new Store(user, "storeName", time, time, price);
        given(storeRepository.existsById(storeId)).willReturn(true);

        List<Menu> menuList = IntStream.rangeClosed(1, 20).mapToObj(i ->
                new Menu("menuName",
                        price,
                        "information",
                        store)).collect(Collectors.toList());

        Page<Menu> page = new PageImpl<>(menuList.subList(0, 10), pageable, menuList.size());

        given(menuRepository.findByStoreIdAndMenuState(pageable, storeId, MenuState.SALE)).willReturn(page);

        // when
        Page<MenuResponse> menuResponses = menuService.findByStoreId(1, 10, storeId);

        // then
        assertEquals(2, menuResponses.getTotalPages());
        assertEquals(20, menuResponses.getTotalElements());
        assertEquals(0, menuResponses.getNumber());
        assertEquals(10, menuResponses.getSize());
        assertTrue(menuResponses.hasNext());
    }

    @Test
    void 메뉴_가게별조회시_찾는_가게가_없으면_에러가_발생한다() {


    }

    @Test
    void 메뉴_수정_테스트() {
        //given
        long userId = 1L;
        long menuId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time = LocalTime.of(9, 30, 30);

        MenuRequest request = new MenuRequest("menuName2", price, "information2");
        AuthUser authUser = new AuthUser(1L, "email@gmail.com", UserRole.OWNER);
        UserRole userRole = authUser.getUserRole();

        User user = new User("email@gmail.com", "123!@#abcde", "userName", userRole);

        Store store = new Store(user, "storeName", time, time, price);

        Menu menu = new Menu("menuName", price, "information", store);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));

        menu.update("menuName2" , price, "information2");

        // when
         menuService.updateMenu(userId, menuId, request);

        // then
        assertEquals("menuName2", menu.getMenuName());
        assertEquals("information2", menu.getInformation());

        }

    @Test
    void 메뉴_삭제_테스트() {

        long userId = 1L;
        long menuId = 1L;
        BigDecimal price = BigDecimal.valueOf(10000);
        LocalTime time = LocalTime.of(9, 30, 30);

        MenuRequest request = new MenuRequest("menuName", price, "information");
        UserRole userRole = authUser.getUserRole();

        User user = new User("email@gmail.com", "123!@#abcde", "userName", userRole);
        Store store = new Store(user, "storeName", time, time, price);

        Menu menu = new Menu("menuName", price, "information", store);

        List<Store> stores = Lists.newArrayList(store);//1개짜리 store List.

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(menuRepository.findById(anyLong())).willReturn(Optional.of(menu));
        given(storeRepository.findByUserId(anyLong())).willReturn(stores);

        menuService.deleteMenu(userId, menuId);

    }

    @Test
    void 메뉴_삭제시_찾는_유저가_없으면_에러가_발생한다() {

    }

    @Test
    void 메뉴_삭제시_찾는_메뉴가_없으면_에러가_발생한다() {

    }

    @Test
    void 메뉴_삭제시_메뉴의_가게와_로그인한_유저의_가게가_다르면_void를_반환한다() {

    }

}
