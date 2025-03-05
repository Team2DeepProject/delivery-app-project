package com.example.deliveryappproject.domain.bookmark.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@Table(name = "bookmarks")
public class Bookmark extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Builder
    public Bookmark(User user, Store store) {
        this.user = user;
        this.store = store;
    }
}
