package com.shaxian.auth;

import com.shaxian.entity.Employee;

/**
 * 用户会话管理接口
 * 为后续支持 Redis 存储预留扩展点
 */
public interface UserSessionManager {

    /**
     * 创建用户会话
     *
     * @param employee 员工实体
     * @return 用户会话对象
     */
    UserSession createSession(Employee employee);

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
}
