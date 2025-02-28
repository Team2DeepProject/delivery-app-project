package com.example.deliveryappproject.domain.auth.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import com.example.deliveryappproject.domain.auth.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "refresh_tokens")
public class RefreshToken extends Timestamped {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private Long userId;
    private String token;

    @Enumerated(STRING)
    private TokenStatus tokenStatus;

    private LocalDateTime expiresAt;

    public RefreshToken(Long userId) {
        this.userId = userId;
        this.token = UUID.randomUUID().toString();
        this.tokenStatus = TokenStatus.VALID;
        this.expiresAt = LocalDateTime.now().plusDays(7);
    }

    public void updateTokenStatus(TokenStatus tokenStatus){
        this.tokenStatus = tokenStatus;
    }
}
