package com.shaxian.biz.auth;

import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;

import java.util.List;

/**
 * BIZ用户会话管理接口
 * 为后续支持 Redis 存储预留扩展点
 */
public interface BizUserSessionManager {

    /**
     * 创建用户会话
     *
     * @param user 用户实体
     * @param tenant 租户实体
     * @return 用户会话对象
     */
    UserSession createSession(User user, Tenant tenant);

    /**
     * 根据 sessionId 获取用户会话
     *
     * @param sessionId 会话ID
     * @return 用户会话对象，如果不存在则返回 null
     */
    UserSession getSession(String sessionId);

    /**
     * 移除用户会话
     *
     * @param sessionId 会话ID
     */
    void removeSession(String sessionId);

    /**
     * 直接创建并存储用户会话
     *
     * @param userSession 用户会话对象（需要包含sessionId）
     * @return 用户会话对象
     */
    UserSession createSession(UserSession userSession);

    /**
     * 顶号：若给定 phone 已有活跃 session，则移除（用于同手机号在新设备登录时踢掉旧设备）
     *
     * @param phone 手机号
     */
    void evictByPhone(String phone);

    /**
     * 顶上限：把 tenantId 名下的活跃 session 裁剪到 size < keepBelow，被裁的是最早创建的
     * 典型用法：登录前调用 evictOldestForTenant(tid, concurrentLimit) 给即将创建的新 session 留出位置
     *
     * @param tenantId  租户 ID
     * @param keepBelow 期望保留数量上限（不含），即裁剪后活跃 session 数将 < keepBelow
     */
    void evictOldestForTenant(Long tenantId, int keepBelow);

    /**
     * 移除指定租户的所有活跃 session（用于租户被停用 / 改 expires_at / 续费时主动失效）
     *
     * @param tenantId 租户 ID
     */
    void evictByTenantId(Long tenantId);

    /**
     * 列出指定租户的所有活跃 session（用于管理端租户详情页展示）
     *
     * @param tenantId 租户 ID
     * @return UserSession 列表（按创建时间升序，过滤掉已失效的）
     */
    List<UserSession> listActiveSessionsByTenant(Long tenantId);
}
