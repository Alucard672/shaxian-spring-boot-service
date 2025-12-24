package com.shaxian.biz.config;

import com.shaxian.biz.util.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Hibernate多租户标识符解析器
 * 从TenantContext的ThreadLocal中获取当前租户ID
 * 在启动阶段（租户ID为null时）返回默认租户ID 1
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    /**
     * 默认租户ID，用于启动阶段
     */
    private static final String DEFAULT_TENANT_ID = "1";

    @Override
    public String resolveCurrentTenantIdentifier() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            // 启动阶段（还没有HTTP请求），返回默认租户ID
            return DEFAULT_TENANT_ID;
        }
        return tenantId.toString();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        // 返回true表示在租户标识符改变时，验证现有会话
        return true;
    }
}

