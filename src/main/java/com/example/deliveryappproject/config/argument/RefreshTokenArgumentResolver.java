package com.example.deliveryappproject.config.argument;

import com.example.deliveryappproject.common.annotation.RefreshToken;
import com.example.deliveryappproject.common.exception.UnauthorizedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class RefreshTokenArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasRefreshTokenAnnotation = parameter.getParameterAnnotation(RefreshToken.class) != null;
        boolean isStringType = parameter.getParameterType().equals(String.class);

        if (hasRefreshTokenAnnotation != isStringType) {
            throw new UnauthorizedException("@RefreshToken과 String 타입은 함께 사용되어야 합니다.");
        }
        return hasRefreshTokenAnnotation;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        throw new UnauthorizedException("리프레시 토큰이 존재하지 않습니다. 다시 로그인 해주세요.");
    }
}
