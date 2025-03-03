package com.example.deliveryappproject.domain.store.service;

import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.BadRequestException;
import com.example.deliveryappproject.domain.store.dto.request.StoreCreateRequest;
import com.example.deliveryappproject.domain.store.dto.response.StoreGetAllResponse;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.store.repository.StoreRepository;
import com.example.deliveryappproject.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {

    private final StoreRepository storeRepository;

    public void createStore(AuthUser authUser, StoreCreateRequest storeCreateRequest) {

        User user = new User(authUser.getId());

        List<Store> storeList = storeRepository.findByUserId(user.getId());

        if (storeList.size() >= 3) {
            throw new BadRequestException("등록된 가게가 3개 이상입니다.");
        }

        Store store = storeCreateRequest.toEntity(user);
        storeRepository.save(store);
    }

    public Page<StoreGetAllResponse> getAllStore(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Store> storePage = storeRepository.findAllByOrderByModifiedAtDesc(pageable);
        return storePage.map(StoreGetAllResponse::new);
    }
}
