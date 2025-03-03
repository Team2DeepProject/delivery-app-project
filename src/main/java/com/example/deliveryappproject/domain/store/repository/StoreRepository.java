package com.example.deliveryappproject.domain.store.repository;

import com.example.deliveryappproject.domain.store.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s " +
            "WHERE s.user.id = :userId " +
            "AND s.storeState = 'ACTIVE'")
    List<Store> findByUserId(@Param("userId") Long id);

    @EntityGraph(attributePaths = {"user"})
    @Query("SELECT s FROM Store s " +
            "LEFT JOIN FETCH s.user u " +
            "WHERE s.storeState = 'ACTIVE' " +
            "ORDER BY s.modifiedAt DESC")
    Page<Store> findAllByOrderByModifiedAtDesc(Pageable pageable);

    @Query("SELECT s FROM Store s " +
            "WHERE s.id = :storeId " +
            "AND s.storeState = 'ACTIVE'")
    Optional<Store> findById(@Param("storeId") Long id);
}
