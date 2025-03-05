package com.example.deliveryappproject.domain.store.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import com.example.deliveryappproject.domain.bookmark.service.BookmarkCountService;
import com.example.deliveryappproject.domain.menu.dto.response.MenuResponse;
import com.example.deliveryappproject.domain.menu.enums.MenuState;
import com.example.deliveryappproject.domain.menu.service.MenuService;
import com.example.deliveryappproject.domain.store.dto.request.StoreCreateRequest;
import com.example.deliveryappproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetResponse;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.entity.StoreState;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {

    @Mock
    private StoreRepository storeRepository;
    @Mock
    private BookmarkCountService bookmarkCountService;
    @Mock
    private MenuService menuService;
    @InjectMocks
    private StoreService storeService;

    /* createStore */
    @Test
    void 가게작성_등록가게_3개_미만이면_성공() {
        // given
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        StoreCreateRequest storeCreateRequest = new StoreCreateRequest("store name", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(20000));

        List<Store> storeList = List.of(new Store(), new Store());

        given(storeRepository.findByUserId(anyLong())).willReturn(storeList);

        // when
        storeService.createStore(authUser, storeCreateRequest);
    }

    @Test
    void 가게작성_등록가게_3개_이상이면_실패() {
        // given
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        StoreCreateRequest storeCreateRequest = new StoreCreateRequest("store name", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(20000));

        List<Store> storeList = List.of(new Store(), new Store(), new Store());

        given(storeRepository.findByUserId(anyLong())).willReturn(storeList);

        // when & then
        assertThrows(BadRequestException.class,
                () -> storeService.createStore(authUser, storeCreateRequest),
                "등록된 가게가 3개 이상입니다.");
    }

    /* getAllStore */
    @Test
    void 가게다건조회_가게목록과_즐겨찾기수_조회_성공() {
        // given
        Store store1 = new Store(1L, "한식가게", LocalTime.of(8, 0), LocalTime.of(21, 0), BigDecimal.valueOf(20000));
        Store store2 = new Store(2L, "중식가게", LocalTime.of(10, 0), LocalTime.of(22, 0), BigDecimal.valueOf(15000));

        int bookmarkCount1 = 10;
        int bookmarkCount2 = 5;
        List<Store> stores = List.of(store1, store2);
        Page<Store> storePage = new PageImpl<>(stores);

        Pageable pageable = PageRequest.of(0, 10);
        given(storeRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(storePage);
        given(bookmarkCountService.findByStoreId(store1.getId())).willReturn(Collections.nCopies(bookmarkCount1, new Bookmark(new User(1L), store1)));
        given(bookmarkCountService.findByStoreId(store2.getId())).willReturn(Collections.nCopies(bookmarkCount2, new Bookmark(new User(1L), store2)));

        // when
        Page<StoreGetAllResponse> result = storeService.getAllStore(1, 10);

        // when
        assertEquals(2, result.getTotalElements());
        assertEquals("한식가게", result.getContent().get(0).getStoreName());
        assertEquals(bookmarkCount1, result.getContent().get(0).getBookmarkCount());
        assertEquals("중식가게", result.getContent().get(1).getStoreName());
        assertEquals(bookmarkCount2, result.getContent().get(1).getBookmarkCount());

        verify(storeRepository, times(1)).findAllByOrderByModifiedAtDesc(pageable);
        verify(bookmarkCountService, times(1)).findByStoreId(store1.getId());
        verify(bookmarkCountService, times(1)).findByStoreId(store2.getId());
    }

    /* getStore */
    @Test
    void 가게단건조회_가게정보와_즐겨찾기수와_메뉴목록조회_성공() {
        // given
        Long storeId = 1L;
        Store store = new Store(storeId, "한식가게", LocalTime.of(8, 0), LocalTime.of(21, 0), BigDecimal.valueOf(20000));
        int bookmarkCount = 10;
        int page = 1;
        int size = 10;

        MenuResponse menu1 = new MenuResponse(1L, "menu1", BigDecimal.valueOf(7000), "정보1", String.valueOf(MenuState.SALE),"한식가게");
        MenuResponse menu2 = new MenuResponse(2L, "menu2", BigDecimal.valueOf(8000), "정보2", String.valueOf(MenuState.SALE),"한식가게");
        List<MenuResponse> menus = List.of(menu1, menu2);
        Page<MenuResponse> menuPage = new PageImpl<>(menus);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));
        given(bookmarkCountService.findByStoreId(storeId)).willReturn(Collections.nCopies(bookmarkCount, new Bookmark(new User(1L), new Store()))); // List<Bookmark> 반환
        given(menuService.findByStoreId(anyInt(), anyInt(), anyLong())).willReturn(menuPage);

        // When
        StoreGetResponse<Page<MenuResponse>> result = storeService.getStore(storeId, page, size);

        // Then
        assertEquals(store.getStoreName(), result.getStoreName());
        assertEquals(bookmarkCount, result.getBookmarkCount());
        assertEquals(store.getOpenAt(), result.getOpenAt());
        assertEquals(store.getCloseAt(), result.getCloseAt());
        assertEquals(store.getMinOrderPrice().intValue(), result.getMinOrderPrice());
        assertEquals(menuPage, result.getMenu());

        verify(storeRepository, times(1)).findById(storeId);
        verify(bookmarkCountService, times(1)).findByStoreId(storeId);
        verify(menuService, times(1)).findByStoreId(page, size, storeId);
    }

    /* updateStore */
    @Test
    void 가게수정_모든_입력값_수정_성공() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        String newStoreName = "newStoreName";
        LocalTime newOpenAt = LocalTime.of(9, 0);
        LocalTime newCloseAt = LocalTime.of(21, 0);
        BigDecimal newMinOrderPrice = BigDecimal.valueOf(20000);

        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(newStoreName, newOpenAt, newCloseAt, newMinOrderPrice);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newStoreName, store.getStoreName());
        assertEquals(newOpenAt, store.getOpenAt());
        assertEquals(newCloseAt, store.getCloseAt());
        assertEquals(newMinOrderPrice, store.getMinOrderPrice());
    }

    @Test
    void 가게수정_작성한_유저가_아닐시_실패() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(2L), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        String newStoreName = "newStoreName";
        LocalTime newOpenAt = LocalTime.of(9, 0);
        LocalTime newCloseAt = LocalTime.of(21, 0);
        BigDecimal newMinOrderPrice = BigDecimal.valueOf(20000);

        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(newStoreName, newOpenAt, newCloseAt, newMinOrderPrice);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> storeService.updateStore(authUser, storeId, storeUpdateRequest),
                "수정 가능한 유저가 아닙니다.");
    }

    @Test
    void 가게수정_가게이름만_수정시_성공() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        String newStoreName = "newStoreName";
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(newStoreName, null, null, null);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(storeUpdateRequest.getStoreName(), store.getStoreName());
    }

    @Test
    void 가게수정_오픈시간만_수정시_성공() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        LocalTime newOpenAt = LocalTime.of(9, 0);
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(null, newOpenAt, null, null);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newOpenAt, store.getOpenAt());
    }

    @Test
    void 가게수정_닫는시간만_수정시_성공() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        LocalTime newCloseAt = LocalTime.of(21, 0);
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(null, null, newCloseAt, null);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newCloseAt, store.getCloseAt());
    }

    @Test
    void 가게수정_최소주문금액만_수정시_성공() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        BigDecimal newMinOrderPrice = BigDecimal.valueOf(20000);
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(null, null, null, newMinOrderPrice);

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newMinOrderPrice, store.getMinOrderPrice());
    }

    /* deleteStore */
    @Test
    void 가게삭제_등록된_가게삭제_성공() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "storeName", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when
        storeService.deleteStore(authUser, storeId);

        // then
        assertEquals(StoreState.CLOSED, store.getStoreState());
    }

    @Test
    void 가게삭제_작성한_유저가_아닐시_실패() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(2L), "storeName", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        given(storeRepository.findById(anyLong())).willReturn(Optional.of(store));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> storeService.deleteStore(authUser, storeId),
                "삭제 가능한 유저가 아닙니다.");
    }

    @Test
    void 가게삭제_가게가_없을경우_실패() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);

        given(storeRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> storeService.deleteStore(authUser, storeId),
                "Not Found Store");
    }
}
