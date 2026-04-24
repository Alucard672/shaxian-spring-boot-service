package com.shaxian.tech.web;

import com.shaxian.biz.auth.BizSessionAuthInterceptor;
import com.shaxian.biz.auth.BizUserSessionArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final BizSessionAuthInterceptor bizSessionAuthInterceptor;
    private final BizUserSessionArgumentResolver bizUserSessionArgumentResolver;

    public WebConfig(BizSessionAuthInterceptor bizSessionAuthInterceptor,
                     BizUserSessionArgumentResolver bizUserSessionArgumentResolver) {
        this.bizSessionAuthInterceptor = bizSessionAuthInterceptor;
        this.bizUserSessionArgumentResolver = bizUserSessionArgumentResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/biz/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(bizSessionAuthInterceptor)
                .addPathPatterns("/biz/api/**")
                .excludePathPatterns(
                        "/biz/api/auth/login",
                        "/biz/api/auth/register",
                        "/biz/api/products/share/**",
                        "/error",
                        "/health",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(bizUserSessionArgumentResolver);
    }
}
