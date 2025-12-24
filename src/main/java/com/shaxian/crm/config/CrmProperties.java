package com.shaxian.crm.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

/**
 * CRM配置属性类
 * 读取application.yml中crm开头的配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "crm")
public class CrmProperties {
    
    /**
     * 管理员手机号列表
     */
    private Set<String> adminPhones;
}
