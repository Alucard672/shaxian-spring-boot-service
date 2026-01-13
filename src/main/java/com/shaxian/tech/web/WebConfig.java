package com.shaxian.tech.web;

import com.shaxian.biz.auth.BizSessionAuthInterceptor;
import com.shaxian.biz.auth.BizUserSessionArgumentResolver;
import com.shaxian.crm.auth.CrmSessionAuthInterceptor;
import com.shaxian.crm.auth.CrmUserSessionArgumentResolver;
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
    private final CrmSessionAuthInterceptor crmSessionAuthInterceptor;
    private final CrmUserSessionArgumentResolver crmUserSessionArgumentResolver;

    public WebConfig(BizSessionAuthInterceptor bizSessionAuthInterceptor,
                     BizUserSessionArgumentResolver bizUserSessionArgumentResolver,
                     CrmSessionAuthInterceptor crmSessionAuthInterceptor,
                     CrmUserSessionArgumentResolver crmUserSessionArgumentResolver) {
        this.bizSessionAuthInterceptor = bizSessionAuthInterceptor;
        this.bizUserSessionArgumentResolver = bizUserSessionArgumentResolver;
        this.crmSessionAuthInterceptor = crmSessionAuthInterceptor;
        this.crmUserSessionArgumentResolver = crmUserSessionArgumentResolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/biz/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
        registry.addMapping("/crm/api/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // BIZ 会话拦截器
        registry.addInterceptor(bizSessionAuthInterceptor)
                .addPathPatterns("/biz/api/**")
                .excludePathPatterns(
                        "/biz/api/auth/login",
                        "/biz/api/auth/register",
                        "/error",
                        "/health",
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                );
        
        // CRM 会话拦截器
        registry.addInterceptor(crmSessionAuthInterceptor)
                .addPathPatterns("/crm/api/**")
                .excludePathPatterns(
                        "/crm/api/auth/login",
                        "/crm/api/auth/logout",
                        "/crm/api/auth/session",
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
        resolvers.add(crmUserSessionArgumentResolver);
    }
}

