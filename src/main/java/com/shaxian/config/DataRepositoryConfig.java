package com.shaxian.config;

import com.shaxian.tech.hibernate.DefaultValueInterceptor;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据访问层配置
 * 用于其他数据访问相关配置
 * JPA仓库配置已在主应用类中定义
 */
@Configuration
public class DataRepositoryConfig {

    @Bean
    public DefaultValueInterceptor defaultValueInterceptor() {
        return new DefaultValueInterceptor();
    }

    @Bean
    public org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
    hibernateDefaultValueInterceptorCustomizer(DefaultValueInterceptor interceptor) {
        return hibernateProperties ->
                hibernateProperties.put(AvailableSettings.INTERCEPTOR, interceptor);
    }
}