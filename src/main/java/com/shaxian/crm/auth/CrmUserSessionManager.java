package com.shaxian.crm.auth;

/**
 * CRM用户会话管理接口
 * 为后续支持 Redis 存储预留扩展点
 */
public interface CrmUserSessionManager {

    /**
     * 根据 sessionId 获取CRM用户会话
     *
     * @param sessionId 会话ID
     * @return CRM用户会话对象，如果不存在则返回 null
     */
    CrmUserSession getSession(String sessionId);

    /**
     * 移除CRM用户会话
     *
     * @param sessionId 会话ID
     */
    void removeSession(String sessionId);

    /**
     * 创建并存储CRM用户会话
     *
     * @param crmUserSession CRM用户会话对象（需要包含sessionId）
     * @return CRM用户会话对象
     */
    CrmUserSession createSession(CrmUserSession crmUserSession);
}
