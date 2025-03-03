package com.example.deliveryappproject.domain.category.repository;

import com.example.deliveryappproject.domain.category.entity.CategoryStore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoryStoreRepository extends JpaRepository<CategoryStore, Long>  {

    @Query("SELECT c FROM CategoryStore c " +
            "WHERE c.category.id = :categoryId " +
            "AND c.store.id = :storeId")
    Optional<CategoryStore> findByStoreAndCategory(
            @Param("categoryId") Long categoryId,
            @Param("storeId") Long storeId
    );
}
