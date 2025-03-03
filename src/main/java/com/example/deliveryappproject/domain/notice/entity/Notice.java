package com.example.deliveryappproject.domain.notice.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import com.example.deliveryappproject.domain.store.entity.Store;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "notices")
public class Notice extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String contents;

    @Builder
    public Notice(Store store, String title, String contents) {
        this.store = store;
        this.title = title;
        this.contents = contents;
    }

    public void updateNotice(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
