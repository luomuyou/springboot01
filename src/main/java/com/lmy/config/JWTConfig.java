package com.lmy.config;

import com.lmy.JWTInterceptor.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
public class JWTConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) { //拦截器注入
        registry.addInterceptor(new JWTInterceptor())   //注入自定义拦截器类
                .excludePathPatterns("/JWT/JWTlogin") //登录请求之外
                .addPathPatterns("/JWT/JWTtest");     //拦截所有请求

    }
}
