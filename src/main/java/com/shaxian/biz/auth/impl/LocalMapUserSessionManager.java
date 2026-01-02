package com.shaxian.biz.auth.impl;

import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.auth.UserSessionManager;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于本地内存 Map 的用户会话管理实现
 */
@Service
public class LocalMapUserSessionManager implements UserSessionManager {

    /**
     * 会话存储：key 为 sessionId，value 为 UserSession
     */
    private final ConcurrentHashMap<String, UserSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public UserSession createSession(User user, Tenant tenant) {
        String sessionId = UUID.randomUUID().toString();
        UserSession userSession = new UserSession(
                sessionId,
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                null, // role 从User中获取，暂时为null
                null, // position 从User中获取，暂时为null
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                tenant != null ? tenant.getCode() : null
        );
        sessionMap.put(sessionId, userSession);
        return userSession;
    }

    @Override
    public UserSession getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        return sessionMap.get(sessionId);
    }

    @Override
    public void removeSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            sessionMap.remove(sessionId);
        }
    }

    @Override
    public UserSession createSession(UserSession userSession) {
        if (userSession == null || userSession.getSessionId() == null || userSession.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("UserSession和sessionId不能为空");
        }
        sessionMap.put(userSession.getSessionId(), userSession);
        return userSession;
    }

    /**
     * 获取当前会话数量（用于监控）
     *
     * @return 会话数量
     */
    public int getSessionCount() {
        return sessionMap.size();
    }
}
