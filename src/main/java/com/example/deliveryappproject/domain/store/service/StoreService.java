package com.example.deliveryappproject.domain.store.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.NotFoundException;
import com.example.deliveryappproject.domain.bookmark.service.BookmarkCountService;
import com.example.deliveryappproject.domain.bookmark.service.BookmarkService;
import com.example.deliveryappproject.domain.menu.dto.MenuResponse;
import com.example.deliveryappproject.domain.menu.service.MenuService;
import com.example.deliveryappproject.domain.store.dto.request.StoreCreateRequest;
import com.example.deliveryappproject.domain.store.dto.request.StoreUpdateRequest;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetResponse;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import static com.example.deliveryappproject.domain.store.entity.StoreState.CLOSED;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;
    private final BookmarkCountService bookmarkCountService;
    private final MenuService menuService;

    @Transactional
    public void createStore(AuthUser authUser, StoreCreateRequest storeCreateRequest) {

        User user = new User(authUser.getId());

        List<Store> storeList = storeRepository.findByUserId(user.getId());

        if (storeList.size() >= 3) {
            throw new BadRequestException("등록된 가게가 3개 이상입니다.");
        }

        Store store = StoreCreateRequest.toEntity(user, storeCreateRequest);
        storeRepository.save(store);
    }

    @Transactional(readOnly = true)
    public Page<StoreGetAllResponse> getAllStore(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Store> storePage = storeRepository.findAllByOrderByModifiedAtDesc(pageable);
        return storePage.map( store -> {
            int bookmarkCount = bookmarkCountService.findByStoreId(store.getId()).size();
            return StoreGetAllResponse.fromDto(store, bookmarkCount);
        });
    }

    @Transactional
    public StoreGetResponse<Page<MenuResponse>> getStore(Long storeId, int page, int size) {
        Store findStore = findStoreByIdOrElseThrow(storeId);
        int bookmarkCount = bookmarkCountService.findByStoreId(storeId).size();
        Page<MenuResponse> menuPage = menuService.findByStoreId(page, size, storeId);

        return StoreGetResponse.fromDto(findStore, bookmarkCount, menuPage);
    }

    @Transactional
    public void updateStore(AuthUser authUser, Long storeId, StoreUpdateRequest storeUpdateRequest) {
        Store findStore = findStoreByIdOrElseThrow(storeId);

        if (!Objects.equals(findStore.getUser().getId(), authUser.getId())) {
            throw new ForbiddenException("수정 가능한 유저가 아닙니다.");
        }

        String storeName = storeUpdateRequest.getStoreName() != null ?
                storeUpdateRequest.getStoreName() : findStore.getStoreName();
        LocalTime openAt = storeUpdateRequest.getOpenAt() != null ?
                storeUpdateRequest.getOpenAt() : findStore.getOpenAt();
        LocalTime closeAt = storeUpdateRequest.getCloseAt() != null ?
                storeUpdateRequest.getCloseAt() : findStore.getCloseAt();
        BigDecimal minOrderPrice = storeUpdateRequest.getMinOrderPrice() != null ?
                storeUpdateRequest.getMinOrderPrice() : findStore.getMinOrderPrice();

        findStore.updateStore(storeName, openAt, closeAt, minOrderPrice);
    }

    @Transactional
    public void deleteStore(AuthUser authUser, Long storeId) {
        Store findStore = findStoreByIdOrElseThrow(storeId);

        if (!Objects.equals(findStore.getUser().getId(), authUser.getId())) {
            throw new ForbiddenException("삭제 가능한 유저가 아닙니다.");
        }

        findStore.updateStoreState(CLOSED);
    }

    public Store findStoreByIdOrElseThrow(Long id) {
        return storeRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Not Found Store")
        );
    }
}
