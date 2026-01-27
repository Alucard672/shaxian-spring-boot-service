package com.shaxian.crm.auth.impl;

import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.auth.CrmUserSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的CRM用户会话管理实现
 */
@Service
public class RedisCrmUserSessionManager implements CrmUserSessionManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 会话过期时间，默认2小时（单位：秒）
     */
    @Value("${crm.session.timeout:7200}")
    private int sessionTimeout;

    /**
     * Redis键前缀
     */
    private static final String SESSION_KEY_PREFIX = "crm:session:";

    @Override
    public CrmUserSession getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        Object sessionObj = redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + sessionId);
        return sessionObj instanceof CrmUserSession ? (CrmUserSession) sessionObj : null;
    }

    @Override
    public void removeSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
        }
    }

    @Override
    public CrmUserSession createSession(CrmUserSession crmUserSession) {
        if (crmUserSession == null || crmUserSession.getSessionId() == null || crmUserSession.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("CrmUserSession和sessionId不能为空");
        }
        redisTemplate.opsForValue().set(
            SESSION_KEY_PREFIX + crmUserSession.getSessionId(),
            crmUserSession,
            sessionTimeout,
            TimeUnit.SECONDS
        );
        return crmUserSession;
    }

    /**
     * 刷新会话过期时间
     *
     * @param sessionId 会话ID
     */
    public void refreshSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            CrmUserSession session = getSession(sessionId);
            if (session != null) {
                redisTemplate.expire(SESSION_KEY_PREFIX + sessionId, sessionTimeout, TimeUnit.SECONDS);
            }
        }
    }
}