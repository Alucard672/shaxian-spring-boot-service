package com.shaxian.auth.impl;

import com.shaxian.auth.UserSession;
import com.shaxian.auth.UserSessionManager;
import com.shaxian.entity.Employee;
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
    public UserSession createSession(Employee employee) {
        String sessionId = UUID.randomUUID().toString();
        UserSession userSession = new UserSession(
                sessionId,
                employee.getId(),
                employee.getName(),
                employee.getPhone(),
                employee.getEmail(),
                employee.getRole(),
                employee.getPosition()
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

    /**
     * 获取当前会话数量（用于监控）
     *
     * @return 会话数量
     */
    public int getSessionCount() {
        return sessionMap.size();
    }
}
