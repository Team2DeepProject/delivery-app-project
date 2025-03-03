package com.example.deliveryappproject.domain.user.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="users")    // 수정
public class User extends Timestamped { // Timestamped 추가

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;        // userId-> id로 수정

    @Column(unique = true)      // 이메일 유니크화
    private String email;
    private String password;
    private String userName;

    @Enumerated(EnumType.STRING) // 추가
    private UserRole userRole;
    private int point;

    public User(Long id) {
        this.id = id;
    }

    public User(String email, String password, String userName, UserRole userRole){
        this.email=email;
        this.password=password;
        this.userName=userName;
        this.userRole=userRole;
        this.point=0;
    }

}
