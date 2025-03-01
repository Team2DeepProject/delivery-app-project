package com.example.deliveryappproject.bookmark.repository;

import com.example.deliveryappproject.bookmark.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    boolean existsByUserIdAndStoreId(Long userId, Long storeId);

    void deleteByUserIdAndStoreId(Long userId, Long storeId);

    List<Bookmark> findByUserId(Long userId);
}
