package com.example.deliveryappproject.domain.bookmark.repository;

import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndStoreId(Long userId, Long storeId);

    Page<Bookmark> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Bookmark b WHERE b.store.id = :storeId")
    int countByStoreId(@Param("storeId") Long storeId);
}
