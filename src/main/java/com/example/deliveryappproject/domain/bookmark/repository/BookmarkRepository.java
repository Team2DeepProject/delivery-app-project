package com.example.deliveryappproject.domain.bookmark.repository;

import com.example.deliveryappproject.domain.bookmark.entity.Bookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.print.Pageable;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserIdAndStoreId(Long userId, Long storeId);

    Page<Bookmark> findByUserId(Long userId, Pageable pageable);
}
