package com.shaxian.biz.auth.impl;

import com.shaxian.biz.auth.BizUserSessionManager;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 基于本地内存 Map 的BIZ用户会话管理实现
 */
@Service
public class LocalMapBizUserSessionManager implements BizUserSessionManager {

    /**
     * 会话存储：key 为 sessionId，value 为 UserSession
     */
    private final ConcurrentHashMap<String, UserSession> sessionMap = new ConcurrentHashMap<>();

    /**
     * 顶号索引：phone → sessionId
     */
    private final ConcurrentHashMap<String, String> phoneIndex = new ConcurrentHashMap<>();

    /**
     * 顶上限索引：tenantId → 按 createdAt 升序的 SessionRef 集合
     */
    private final ConcurrentHashMap<Long, ConcurrentSkipListSet<SessionRef>> tenantIndex = new ConcurrentHashMap<>();

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
                null, // role 从User中获取，暂时为null
                null, // position 从User中获取，暂时为null
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
        sessionMap.put(sessionId, userSession);
        indexSession(userSession);
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
        if (sessionId == null || sessionId.isEmpty()) {
            return;
        }
        UserSession removed = sessionMap.remove(sessionId);
        if (removed != null) {
            unindexSession(removed);
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
        sessionMap.put(userSession.getSessionId(), userSession);
        indexSession(userSession);
        return userSession;
    }

    @Override
    public void evictByPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return;
        }
        String sid = phoneIndex.get(phone);
        if (sid != null) {
            removeSession(sid);
        }
    }

    @Override
    public void evictOldestForTenant(Long tenantId, int keepBelow) {
        if (tenantId == null || keepBelow < 0) {
            return;
        }
        ConcurrentSkipListSet<SessionRef> refs = tenantIndex.get(tenantId);
        if (refs == null) {
            return;
        }
        // 由于 ConcurrentSkipListSet 是按 createdAt 升序，pollFirst 移除最早
        while (refs.size() >= keepBelow) {
            SessionRef oldest = refs.pollFirst();
            if (oldest == null) {
                break;
            }
            // 同步移除 sessionMap + phoneIndex（removeSession 内部会再次尝试从 tenantIndex 移除，
            // 但因为我们已经 pollFirst，那次操作会 no-op，幂等安全）
            UserSession s = sessionMap.remove(oldest.sessionId);
            if (s != null) {
                phoneIndex.remove(s.getPhone(), oldest.sessionId);
            }
        }
    }

    @Override
    public void evictByTenantId(Long tenantId) {
        if (tenantId == null) {
            return;
        }
        ConcurrentSkipListSet<SessionRef> refs = tenantIndex.remove(tenantId);
        if (refs == null) {
            return;
        }
        for (SessionRef ref : refs) {
            UserSession s = sessionMap.remove(ref.sessionId);
            if (s != null) {
                phoneIndex.remove(s.getPhone(), ref.sessionId);
            }
        }
    }

    @Override
    public List<UserSession> listActiveSessionsByTenant(Long tenantId) {
        if (tenantId == null) {
            return List.of();
        }
        ConcurrentSkipListSet<SessionRef> refs = tenantIndex.get(tenantId);
        if (refs == null) {
            return List.of();
        }
        List<UserSession> result = new ArrayList<>(refs.size());
        for (SessionRef ref : refs) {
            UserSession s = sessionMap.get(ref.sessionId);
            if (s != null) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * 获取当前会话数量（用于监控）
     */
    public int getSessionCount() {
        return sessionMap.size();
    }

    // ============================================================
    // 内部维护方法
    // ============================================================

    private void indexSession(UserSession s) {
        if (s.getPhone() != null && !s.getPhone().isEmpty()) {
            phoneIndex.put(s.getPhone(), s.getSessionId());
        }
        // 超管不入 tenant 并发索引（plan-decisions P5）
        if (!s.isSuperAdmin() && s.getTenantId() != null) {
            tenantIndex
                    .computeIfAbsent(s.getTenantId(), k -> new ConcurrentSkipListSet<>())
                    .add(new SessionRef(s.getSessionId(), s.getCreatedAt() != null ? s.getCreatedAt() : LocalDateTime.now()));
        }
    }

    private void unindexSession(UserSession s) {
        if (s.getPhone() != null) {
            phoneIndex.remove(s.getPhone(), s.getSessionId());
        }
        if (s.getTenantId() != null) {
            ConcurrentSkipListSet<SessionRef> refs = tenantIndex.get(s.getTenantId());
            if (refs != null) {
                refs.removeIf(r -> Objects.equals(r.sessionId, s.getSessionId()));
            }
        }
    }

    /**
     * 内部引用：按 (createdAt, sessionId) 排序，sessionId 做 tie-breaker
     */
    private static final class SessionRef implements Comparable<SessionRef> {
        private final String sessionId;
        private final LocalDateTime createdAt;

        SessionRef(String sessionId, LocalDateTime createdAt) {
            this.sessionId = sessionId;
            this.createdAt = createdAt;
        }

        @Override
        public int compareTo(SessionRef o) {
            int c = this.createdAt.compareTo(o.createdAt);
            if (c != 0) return c;
            return this.sessionId.compareTo(o.sessionId);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof SessionRef)) return false;
            return Objects.equals(sessionId, ((SessionRef) o).sessionId);
        }

        @Override
        public int hashCode() {
            return sessionId.hashCode();
        }
    }
}
