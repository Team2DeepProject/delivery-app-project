package com.example.deliveryappproject.domain.category.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import com.example.deliveryappproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "categorys")
public class Category extends Timestamped{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(unique = true)
    private String name;

    @Builder
    public Category(User user, String name) {
        this.user = user;
        this.name = name;
    }

    public Category(Long id) {
        this.id = id;
    }

    public void updateCategoryName(String name) {
        this.name = name;
    }
}
