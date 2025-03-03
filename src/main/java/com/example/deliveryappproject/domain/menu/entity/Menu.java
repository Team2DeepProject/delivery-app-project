package com.example.deliveryappproject.domain.menu.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    public Menu(String menuName, BigDecimal price, String information){
        this.menuName=menuName;
        this.price=price;
        this.information=information;
    }
}
