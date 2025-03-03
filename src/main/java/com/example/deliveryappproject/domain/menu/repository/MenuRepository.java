package com.example.deliveryappproject.domain.menu.repository;

import com.example.deliveryappproject.domain.menu.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Long>  {
}
