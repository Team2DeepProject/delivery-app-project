package com.example.deliveryappproject.user.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name="users")
public class User {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String email;
    private String password;
    private String userName;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private int point;

    public User(String email, String password, String userName, UserRole userRole){
        this.email=email;
        this.password=password;
        this.userName=userName;
        this.userRole=userRole;
        this.point=0;
    }

}
