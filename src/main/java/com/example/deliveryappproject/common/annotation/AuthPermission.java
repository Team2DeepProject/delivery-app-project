package com.example.deliveryappproject.common.annotation;

import com.example.deliveryappproject.domain.user.entity.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthPermission {
    UserRole role(); // role() 메서드 정의 확인!
}
