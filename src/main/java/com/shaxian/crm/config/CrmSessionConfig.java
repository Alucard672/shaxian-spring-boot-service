package com.shaxian.crm.config;

import com.shaxian.crm.auth.CrmUserSessionManager;
import com.shaxian.crm.auth.impl.LocalMapCrmUserSessionManager;
import com.shaxian.crm.auth.impl.RedisCrmUserSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CRM会话管理配置类
 * 根据配置决定使用本地Map还是Redis作为会话存储
 */
@Configuration
public class CrmSessionConfig {

    /**
     * 是否启用Redis会话管理，默认为false
     */
    @Value("${crm.session.use-redis:false}")
    private boolean useRedis;

    @Bean
    public CrmUserSessionManager crmUserSessionManager(LocalMapCrmUserSessionManager localMapSessionManager,
                                                      RedisCrmUserSessionManager redisSessionManager) {
        if (useRedis) {
            return redisSessionManager;
        } else {
            return localMapSessionManager;
        }
    }
}