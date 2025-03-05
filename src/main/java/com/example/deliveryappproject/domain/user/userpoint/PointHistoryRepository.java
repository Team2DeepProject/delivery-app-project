package com.example.deliveryappproject.domain.user.userpoint;

import com.example.deliveryappproject.domain.user.userpoint.entity.PointHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
}
