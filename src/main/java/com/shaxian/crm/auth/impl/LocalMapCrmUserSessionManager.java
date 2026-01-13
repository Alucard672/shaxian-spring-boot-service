package com.shaxian.crm.auth.impl;

import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.auth.CrmUserSessionManager;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 基于本地内存 Map 的CRM用户会话管理实现
 */
@Service
public class LocalMapCrmUserSessionManager implements CrmUserSessionManager {

    /**
     * 会话存储：key 为 sessionId，value 为 CrmUserSession
     */
    private final ConcurrentHashMap<String, CrmUserSession> sessionMap = new ConcurrentHashMap<>();

    @Override
    public CrmUserSession getSession(String sessionId) {
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
    public CrmUserSession createSession(CrmUserSession crmUserSession) {
        if (crmUserSession == null || crmUserSession.getSessionId() == null || crmUserSession.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("CrmUserSession和sessionId不能为空");
        }
        sessionMap.put(crmUserSession.getSessionId(), crmUserSession);
        return crmUserSession;
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
