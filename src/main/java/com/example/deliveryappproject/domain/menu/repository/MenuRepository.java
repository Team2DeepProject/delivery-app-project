package com.example.deliveryappproject.domain.menu.repository;

import com.example.deliveryappproject.domain.menu.entity.Menu;
import com.example.deliveryappproject.domain.menu.enums.MenuState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long>  {

    boolean existsByMenuNameAndStoreId(String menuName, Long storeId);

    Page<Menu> findByStoreIdAndMenuState(Pageable pageable, Long storeId, MenuState menuState);
}
