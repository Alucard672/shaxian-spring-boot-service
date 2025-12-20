package com.shaxian.config;

import com.shaxian.util.TenantContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Hibernate多租户标识符解析器
 * 从TenantContext的ThreadLocal中获取当前租户ID
 */
@Component
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver {

    @Override
    public String resolveCurrentTenantIdentifier() {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId == null) {
            throw new IllegalStateException("租户ID不能为空");
        }
        return tenantId.toString();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        // 返回true表示在租户标识符改变时，验证现有会话
        return true;
    }
}

