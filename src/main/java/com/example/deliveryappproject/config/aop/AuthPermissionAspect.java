package com.example.deliveryappproject.config.aop;

import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.dto.AuthUser;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class AuthPermissionAspect {

    // @AuthPermission 어노테이션이 붙은 메소드가 실행될 때마다 권한 체크
    @Around("@annotation(authPermission)")  // @AuthPermission 어노테이션을 찾음
    public Object checkPermission(ProceedingJoinPoint joinPoint, AuthPermission authPermission) throws Throwable {
        // 인증된 사용자 정보 가져오기
        AuthUser authUser = (AuthUser) joinPoint.getArgs()[0];  // 첫 번째 인자가 AuthUser로 들어오는 경우

        // 권한이 일치하지 않으면 예외를 발생시킴
        if (authUser == null || !authUser.getUserRole().equals(authPermission.role())) {
            throw new ForbiddenException("권한이 없습니다.");
        }

        // 권한 체크가 통과되면 원래의 메소드 실행
        return joinPoint.proceed();
    }
}
