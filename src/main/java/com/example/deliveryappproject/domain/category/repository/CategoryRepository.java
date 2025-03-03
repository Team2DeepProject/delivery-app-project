package com.example.deliveryappproject.domain.category.repository;

import com.example.deliveryappproject.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
