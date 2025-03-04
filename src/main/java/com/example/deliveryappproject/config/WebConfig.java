package com.example.deliveryappproject.config;

import com.example.deliveryappproject.config.argument.AuthUserArgumentResolver;
import com.example.deliveryappproject.config.argument.RefreshTokenArgumentResolver;
import com.example.deliveryappproject.config.interceptor.AuthRoleInterceptor;
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
        resolvers.add(new RefreshTokenArgumentResolver());
    }

    /* Interceptor 등록 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthRoleInterceptor())
                .order(1)
                .addPathPatterns("/**");
    }
}
