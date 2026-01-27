package com.shaxian.config;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * 主应用配置类
 * 排除Redis仓库自动配置以避免JPA仓库被误识别
 */
@SpringBootApplication(exclude = {
    RedisRepositoriesAutoConfiguration.class
})
@Configuration
@EnableJpaRepositories(basePackages = {
    "com.shaxian.biz.repository",
    "com.shaxian.crm.repository",
    "com.shaxian.tech.repository"
})
@EntityScan(basePackages = {
    "com.shaxian.biz.entity",
    "com.shaxian.crm.entity",
    "com.shaxian.tech.entity"
})
public class MainApplicationConfig {
    // 主配置类，明确排除Redis仓库自动配置以避免冲突
}