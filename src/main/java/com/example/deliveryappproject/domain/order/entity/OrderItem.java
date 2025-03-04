package com.example.deliveryappproject.domain.order.entity;

import com.example.deliveryappproject.domain.menu.entity.Menu;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int quantity;

    private String menuName;

    private BigDecimal orderPrice;

    private Long menuId;

    private OrderItem(int quantity, String menuName, BigDecimal orderPrice, Long menuId) {
        this.quantity = quantity;
        this.menuName = menuName;
        this.orderPrice = orderPrice;
        this.menuId = menuId;
    }

    public static OrderItem createOrderItem(Menu menu, BigDecimal menuTotalPrice, int quantity) {
        return new OrderItem(quantity,menu.getMenuName(), menuTotalPrice,menu.getId());
    }

    public void setOrder(Order order) {
        this.order = order;
    }
}
