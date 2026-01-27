package com.shaxian.biz.auth.impl;

import com.shaxian.biz.auth.BizUserSessionManager;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis的BIZ用户会话管理实现
 */
@Service
public class RedisBizUserSessionManager implements BizUserSessionManager {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 会话过期时间，默认2小时（单位：秒）
     */
    @Value("${biz.session.timeout:7200}")
    private int sessionTimeout;

    /**
     * Redis键前缀
     */
    private static final String SESSION_KEY_PREFIX = "biz:session:";

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

        redisTemplate.opsForValue().set(
            SESSION_KEY_PREFIX + sessionId,
            userSession,
            sessionTimeout,
            TimeUnit.SECONDS
        );
        return userSession;
    }

    @Override
    public UserSession getSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return null;
        }
        Object sessionObj = redisTemplate.opsForValue().get(SESSION_KEY_PREFIX + sessionId);
        return sessionObj instanceof UserSession ? (UserSession) sessionObj : null;
    }

    @Override
    public void removeSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
        }
    }

    @Override
    public UserSession createSession(UserSession userSession) {
        if (userSession == null || userSession.getSessionId() == null || userSession.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("UserSession和sessionId不能为空");
        }
        redisTemplate.opsForValue().set(
            SESSION_KEY_PREFIX + userSession.getSessionId(),
            userSession,
            sessionTimeout,
            TimeUnit.SECONDS
        );
        return userSession;
    }

    /**
     * 刷新会话过期时间
     *
     * @param sessionId 会话ID
     */
    public void refreshSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            UserSession session = getSession(sessionId);
            if (session != null) {
                redisTemplate.expire(SESSION_KEY_PREFIX + sessionId, sessionTimeout, TimeUnit.SECONDS);
            }
        }
    }
}