package com.example.deliveryappproject.domain.order.repository;

import com.example.deliveryappproject.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT distinct o FROM Order o join fetch o.orderItems where o.id =:orderId")
    Optional<Order> findByIdWithOrderItems(Long orderId);

    @Query("SELECT distinct o FROM Order o join fetch o.store join fetch o.orderItems where o.id =:orderId")
    Optional<Order> findByIdWithStoreWithOrderItems(Long orderId);
}
