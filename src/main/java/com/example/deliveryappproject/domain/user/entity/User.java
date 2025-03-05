package com.example.deliveryappproject.domain.user.entity;

import com.example.deliveryappproject.common.entity.Timestamped;
import com.example.deliveryappproject.domain.store.entity.Store;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import com.example.deliveryappproject.domain.user.enums.UserState;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor
@Table(name="users")    // 수정
public class User extends Timestamped { // Timestamped 추가

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String email;
    private String password;
    private String userName;

    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    private UserState userState;

    private int point;

    @OneToMany(mappedBy = "user")
    private List<Store> storeList;

    public User(Long id) {
        this.id = id;
    }

    public User(String email, String password, String userName, UserRole userRole){
        this.email=email;
        this.password=password;
        this.userName=userName;
        this.userRole=userRole;
        this.point=0;
        this.userState=UserState.ACTIVE;
    }

    public void update(String userName){
        this.userName=userName;
    }

    public void setUserState(UserState userState){
        this.userState=userState;
    }

    public void usePoints(int usedPoints) {
        this.point -= usedPoints;
    }

    public void addPoints(int earnedPoints) {
        this.point += earnedPoints;
    }

    public void updateUserRole(UserRole userRole) { this.userRole=userRole; }
}
