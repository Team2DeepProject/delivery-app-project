package com.example.deliveryappproject.domain.store.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.store.dto.request.StoreCreateRequest;
import com.example.deliveryappproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
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
import java.time.LocalTime;
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
    @InjectMocks
    private StoreService storeService;

    /* createStore */
    @Test
    void createStore에서_등록된_가게가_3개미만일_경우_정상적으로_가게를_저장할_수_있는가() {
        // given
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        StoreCreateRequest storeCreateRequest = new StoreCreateRequest("store name", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(20000));

        List<Store> storeList = List.of(new Store(), new Store());

        given(storeRepository.findByUserId(anyLong())).willReturn(storeList);

        // when
        storeService.createStore(authUser, storeCreateRequest);
    }

    @Test
    void createStore에서_등록된_가게가_3개이상일_경우_BadRequestException를_던지는가() {
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
    void getAllStore에서_정상적으로_가게_목록을_조회할_수_있는가() {
        // given
        int page = 1;
        int size = 10;
        Pageable pageable = PageRequest.of(page - 1, size);

        Store store1 = new Store(new User(1L), "Store A", LocalTime.of(9, 0), LocalTime.of(22, 0), BigDecimal.valueOf(15000));
        Store store2 = new Store(new User(2L), "Store B", LocalTime.of(10, 0), LocalTime.of(23, 0), BigDecimal.valueOf(12000));
        List<Store> storeList = List.of(store1, store2);
        Page<Store> storePage = new PageImpl<>(storeList, pageable, storeList.size());

        given(storeRepository.findAllByOrderByModifiedAtDesc(pageable)).willReturn(storePage);

        // when
        Page<StoreGetAllResponse> result = storeService.getAllStore(page, size);

        // then
        assertNotNull(result);
        verify(storeRepository, times(1)).findAllByOrderByModifiedAtDesc(pageable);
    }

    /* updateStore */
    @Test
    void updateStore에서_모든_입력값을_받아_업데이트_할_수_있는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        String newStoreName = "newStoreName";
        LocalTime newOpenAt = LocalTime.of(9, 0);
        LocalTime newCloseAt = LocalTime.of(21, 0);
        BigDecimal newMinOrderPrice = BigDecimal.valueOf(20000);

        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(newStoreName, newOpenAt, newCloseAt, newMinOrderPrice);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newStoreName, store.getStoreName());
        assertEquals(newOpenAt, store.getOpenAt());
        assertEquals(newCloseAt, store.getCloseAt());
        assertEquals(newMinOrderPrice, store.getMinOrderPrice());
    }

    @Test
    void updateStore에서_가게를_작성한_주인이_아닌_유저에_대해_ForbiddenException를_던질_수_있는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(2L), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        String newStoreName = "newStoreName";
        LocalTime newOpenAt = LocalTime.of(9, 0);
        LocalTime newCloseAt = LocalTime.of(21, 0);
        BigDecimal newMinOrderPrice = BigDecimal.valueOf(20000);

        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(newStoreName, newOpenAt, newCloseAt, newMinOrderPrice);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> storeService.updateStore(authUser, storeId, storeUpdateRequest),
                "수정 가능한 유저가 아닙니다.");
    }

    @Test
    void updateStore에서_가게이름만_수정할_수_있는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        String newStoreName = "newStoreName";
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(newStoreName, null, null, null);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(storeUpdateRequest.getStoreName(), store.getStoreName());
    }

    @Test
    void updateStore에서_오픈시간만_수정할_수_있는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        LocalTime newOpenAt = LocalTime.of(9, 0);
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(null, newOpenAt, null, null);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newOpenAt, store.getOpenAt());
    }

    @Test
    void updateStore에서_마감시간만_수정할_수_있는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        LocalTime newCloseAt = LocalTime.of(21, 0);
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(null, null, newCloseAt, null);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newCloseAt, store.getCloseAt());
    }

    @Test
    void updateStore에서_최소주문금액만_수정할_수_있는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "oldStore", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        BigDecimal newMinOrderPrice = BigDecimal.valueOf(20000);
        StoreUpdateRequest storeUpdateRequest = new StoreUpdateRequest(null, null, null, newMinOrderPrice);

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when
        storeService.updateStore(authUser, storeId, storeUpdateRequest);

        // then
        assertEquals(newMinOrderPrice, store.getMinOrderPrice());
    }

    /* deleteStore */
    @Test
    void deleteStore에서_가게를_정상적으로_삭제할_수_있는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(authUser.getId()), "storeName", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when
        storeService.deleteStore(authUser, storeId);

        // then
        assertEquals(StoreState.CLOSED, store.getStoreState());
    }

    @Test
    void deleteStore에서_가게를_작성한_주인이_아닌_유저에_대해_ForbiddenException를_던지는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);
        Store store = new Store(new User(2L), "storeName", LocalTime.of(8, 0), LocalTime.of(20, 0), BigDecimal.valueOf(15000));

        given(storeRepository.findById(storeId)).willReturn(Optional.of(store));

        // when & then
        assertThrows(ForbiddenException.class,
                () -> storeService.deleteStore(authUser, storeId),
                "삭제 가능한 유저가 아닙니다.");
    }

    @Test
    void deleteStore에서_해당되는_가게가_없을_경우_NotFoundException를_던지는가() {
        // given
        Long storeId = 1L;
        AuthUser authUser = new AuthUser(1L, "email@email.com", UserRole.USER);

        given(storeRepository.findById(storeId)).willReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class,
                () -> storeService.deleteStore(authUser, storeId),
                "Not Found Store");
    }
}
