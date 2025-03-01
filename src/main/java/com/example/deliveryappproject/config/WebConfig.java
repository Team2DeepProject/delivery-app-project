package com.example.deliveryappproject.config;

import com.example.deliveryappproject.config.argument.AuthUserArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /* ArgumentResolver 등록 */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthUserArgumentResolver());
    }

    /* Interceptor 등록 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 커스텀한 어노테이션으로 인가 구분 구현 예정

    }
}
