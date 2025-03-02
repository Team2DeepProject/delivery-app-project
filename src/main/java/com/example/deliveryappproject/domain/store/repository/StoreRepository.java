package com.example.deliveryappproject.domain.store.repository;

import com.example.deliveryappproject.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s " +
            "WHERE s.user.id = :userId " +
            "AND s.storeState = 'ACTIVE'")
    List<Store> findByUserId(@Param("userId") Long id);
}
