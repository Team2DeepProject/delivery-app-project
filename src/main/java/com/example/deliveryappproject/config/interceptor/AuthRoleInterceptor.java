package com.example.deliveryappproject.config.interceptor;

import com.example.deliveryappproject.common.annotation.AuthPermission;
import com.example.deliveryappproject.common.exception.ForbiddenException;
import com.example.deliveryappproject.common.exception.UnauthorizedException;
import com.example.deliveryappproject.domain.user.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthRoleInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        try {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // @AuthPermission 있는지 없는지 확인후 없을시 바로 출력(비로그인자 접근가능 url + USER)
            AuthPermission authPermission = handlerMethod.getMethodAnnotation(AuthPermission.class);
            if (authPermission == null) {
                return true;
            }

            // 토큰에서 유저 정보 가저옴
            UserRole userRole = UserRole.of((String) request.getAttribute("userRole"));
            if (userRole == null) {
                throw new UnauthorizedException("로그인이 필요합니다.");
            }

            // 유저 권한 체크
            if (authPermission.role() != userRole) {
                throw new ForbiddenException("이용 권한이 없습니다.");
            }

            return true;

        } catch (ClassCastException e) {
            throw new BadRequestException("잘못된 요청값입니다.");
        }
    }
}
