package com.example.deliveryappproject.domain.auth.repository;

import com.example.deliveryappproject.domain.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}