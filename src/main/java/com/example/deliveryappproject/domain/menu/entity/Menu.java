package com.example.deliveryappproject.domain.menu.entity;


import com.example.deliveryappproject.domain.menu.enums.MenuState;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Entity
@NoArgsConstructor
@Table(name="menus")
public class Menu  extends Timestamped {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String menuName;

    private BigDecimal price;

    private String information;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Enumerated(EnumType.STRING)
    private MenuState menuState;

    public Menu(String menuName, BigDecimal price, String information, Store store){
        this.menuName=menuName;
        this.price=price;
        this.information=information;
        this.store=store;
        this.menuState=MenuState.SALE;
    }

    public void update(String menuName, BigDecimal price, String information){
        this.menuName=menuName;
        this.price=price;
        this.information=information;
    }

    public void setMenuState(MenuState menuState){
        this.menuState=menuState;
    }
}
