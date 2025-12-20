package com.shaxian.util;

/**
 * 租户上下文工具类
 * 使用ThreadLocal存储当前请求的租户ID
 */
public class TenantContext {
    private static final ThreadLocal<Long> tenantIdHolder = new ThreadLocal<>();

    /**
     * 设置当前线程的租户ID
     */
    public static void setTenantId(Long tenantId) {
        tenantIdHolder.set(tenantId);
    }

    /**
     * 获取当前线程的租户ID
     */
    public static Long getTenantId() {
        return tenantIdHolder.get();
    }

    /**
     * 清理当前线程的租户ID
     * 应在请求处理完成后调用，避免内存泄漏
     */
    public static void clear() {
        tenantIdHolder.remove();
    }
}

