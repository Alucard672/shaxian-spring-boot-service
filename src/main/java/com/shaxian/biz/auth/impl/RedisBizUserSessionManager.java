package com.shaxian.biz.auth.impl;

import com.shaxian.biz.auth.BizUserSessionManager;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

    private static final String SESSION_KEY_PREFIX = "biz:session:";
    private static final String PHONE_KEY_PREFIX   = "biz:phone:";
    private static final String TENANT_KEY_PREFIX  = "biz:tenant:";

    @Override
    public UserSession createSession(User user, Tenant tenant) {
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        UserSession userSession = new UserSession(
                sessionId,
                user.getId(),
                user.getName(),
                user.getPhone(),
                user.getEmail(),
                null,
                null,
                tenant != null ? tenant.getId() : null,
                tenant != null ? tenant.getName() : null,
                tenant != null ? tenant.getCode() : null
        );
        userSession.setSuperAdmin(user.isSuperAdmin());
        if (tenant != null) {
            userSession.setTenantStatus(tenant.getStatus());
            userSession.setTenantExpiresAt(tenant.getExpiresAt());
        }
        userSession.setCreatedAt(now);

        storeAndIndex(userSession);
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
        if (sessionId == null || sessionId.isEmpty()) {
            return;
        }
        UserSession s = getSession(sessionId);
        redisTemplate.delete(SESSION_KEY_PREFIX + sessionId);
        if (s != null) {
            unindex(s);
        }
    }

    @Override
    public UserSession createSession(UserSession userSession) {
        if (userSession == null || userSession.getSessionId() == null || userSession.getSessionId().isEmpty()) {
            throw new IllegalArgumentException("UserSession和sessionId不能为空");
        }
        if (userSession.getCreatedAt() == null) {
            userSession.setCreatedAt(LocalDateTime.now());
        }
        storeAndIndex(userSession);
        return userSession;
    }

    @Override
    public void evictByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return;
        }
        Object sid = redisTemplate.opsForValue().get(PHONE_KEY_PREFIX + phone);
        if (sid instanceof String) {
            removeSession((String) sid);
        }
    }

    @Override
    public void evictOldestForTenant(Long tenantId, int keepBelow) {
        if (tenantId == null || keepBelow < 0) {
            return;
        }
        String zKey = TENANT_KEY_PREFIX + tenantId;
        Long size = redisTemplate.opsForZSet().zCard(zKey);
        if (size == null || size < keepBelow) {
            return;
        }
        long toRemove = size - keepBelow + 1;
        // 取最早的 toRemove 个（score 升序）
        Set<Object> oldest = redisTemplate.opsForZSet().range(zKey, 0, toRemove - 1);
        if (oldest == null) return;
        for (Object sid : oldest) {
            if (sid instanceof String) {
                removeSession((String) sid);
            }
        }
    }

    @Override
    public void evictByTenantId(Long tenantId) {
        if (tenantId == null) return;
        String zKey = TENANT_KEY_PREFIX + tenantId;
        Set<Object> all = redisTemplate.opsForZSet().range(zKey, 0, -1);
        if (all != null) {
            for (Object sid : all) {
                if (sid instanceof String) {
                    removeSession((String) sid);
                }
            }
        }
        redisTemplate.delete(zKey);
    }

    @Override
    public List<UserSession> listActiveSessionsByTenant(Long tenantId) {
        if (tenantId == null) return List.of();
        String zKey = TENANT_KEY_PREFIX + tenantId;
        Set<Object> all = redisTemplate.opsForZSet().range(zKey, 0, -1);
        if (all == null || all.isEmpty()) return List.of();
        List<UserSession> result = new ArrayList<>(all.size());
        for (Object sid : all) {
            if (sid instanceof String) {
                UserSession s = getSession((String) sid);
                if (s != null) {
                    result.add(s);
                }
            }
        }
        return result;
    }

    /**
     * 刷新会话过期时间
     */
    public void refreshSession(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            UserSession session = getSession(sessionId);
            if (session != null) {
                redisTemplate.expire(SESSION_KEY_PREFIX + sessionId, sessionTimeout, TimeUnit.SECONDS);
            }
        }
    }

    // ============================================================
    // 内部辅助
    // ============================================================

    private void storeAndIndex(UserSession s) {
        // 1. 主存储
        redisTemplate.opsForValue().set(
                SESSION_KEY_PREFIX + s.getSessionId(),
                s,
                sessionTimeout, TimeUnit.SECONDS
        );
        // 2. phone 顶号索引（超管和普通用户都建索引，便于同手机号互踢）
        if (s.getPhone() != null && !s.getPhone().isEmpty()) {
            redisTemplate.opsForValue().set(
                    PHONE_KEY_PREFIX + s.getPhone(),
                    s.getSessionId(),
                    sessionTimeout, TimeUnit.SECONDS
            );
        }
        // 3. tenant 并发上限索引（超管不入索引；plan-decisions P5）
        if (!s.isSuperAdmin() && s.getTenantId() != null) {
            double score = s.getCreatedAt() != null
                    ? s.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    : System.currentTimeMillis();
            redisTemplate.opsForZSet().add(
                    TENANT_KEY_PREFIX + s.getTenantId(),
                    s.getSessionId(),
                    score
            );
        }
    }

    private void unindex(UserSession s) {
        if (s.getPhone() != null) {
            // 只在 phone key 指向当前 sessionId 时删除，避免误删后来者
            Object current = redisTemplate.opsForValue().get(PHONE_KEY_PREFIX + s.getPhone());
            if (s.getSessionId().equals(current)) {
                redisTemplate.delete(PHONE_KEY_PREFIX + s.getPhone());
            }
        }
        if (s.getTenantId() != null) {
            redisTemplate.opsForZSet().remove(TENANT_KEY_PREFIX + s.getTenantId(), s.getSessionId());
        }
    }
}
