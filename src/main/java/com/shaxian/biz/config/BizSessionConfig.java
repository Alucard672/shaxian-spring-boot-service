package com.shaxian.biz.config;

import com.shaxian.biz.auth.BizUserSessionManager;
import com.shaxian.biz.auth.impl.LocalMapBizUserSessionManager;
import com.shaxian.biz.auth.impl.RedisBizUserSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BIZ会话管理配置类
 * 根据配置决定使用本地Map还是Redis作为会话存储
 */
@Configuration
public class BizSessionConfig {

    /**
     * 是否启用Redis会话管理，默认为false
     */
    @Value("${biz.session.use-redis:false}")
    private boolean useRedis;

    @Bean
    public BizUserSessionManager bizUserSessionManager(LocalMapBizUserSessionManager localMapSessionManager,
                                                      RedisBizUserSessionManager redisSessionManager) {
        if (useRedis) {
            return redisSessionManager;
        } else {
            return localMapSessionManager;
        }
    }
}