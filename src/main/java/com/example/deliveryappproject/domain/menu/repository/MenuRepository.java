package com.example.deliveryappproject.domain.menu.repository;

import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.entity.MenuState;
import com.example.deliveryappproject.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long>  {

    boolean existsByMenuNameAndStoreId(String menuName, Long storeId);

    List<Menu> findByStoreIdAndMenuState(Long storeId, MenuState menuState);
}
