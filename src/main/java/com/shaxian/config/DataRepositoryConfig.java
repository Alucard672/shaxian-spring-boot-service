package com.shaxian.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

/**
 * 数据访问层配置
 * 明确配置JPA仓库的包路径，避免与Redis仓库冲突
 */
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
public class DataRepositoryConfig {
    // 配置JPA仓库包路径，避免与Redis仓库冲突
}