package com.shaxian.biz.auth;

import com.shaxian.biz.auth.impl.LocalMapBizUserSessionManager;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * LocalMapBizUserSessionManager 单元测试
 *
 * 覆盖：
 *  - 创建 session 后 phoneIndex / tenantIndex 同步维护
 *  - evictByPhone 顶号
 *  - evictOldestForTenant 顶最早裁剪到上限
 *  - 超管不入 tenantIndex
 *  - evictByTenantId 全部移除
 */
class LocalMapBizUserSessionManagerTest {

    private LocalMapBizUserSessionManager manager;

    @BeforeEach
    void setUp() {
        manager = new LocalMapBizUserSessionManager();
    }

    @Test
    void createSession_indexes_updated() {
        User user = newUser(101L, "13000000001");
        Tenant tenant = newTenant(10L, "T1");

        UserSession s = manager.createSession(user, tenant);

        assertNotNull(s.getSessionId());
        assertNotNull(manager.getSession(s.getSessionId()));
        // tenant 内存活 session 数 = 1
        assertEquals(1, manager.listActiveSessionsByTenant(10L).size());
    }

    @Test
    void evictByPhone_kicks_old_session() {
        User user = newUser(101L, "13000000001");
        Tenant tenant = newTenant(10L, "T1");

        UserSession first = manager.createSession(user, tenant);

        // 同 phone 第二次登录前，先顶号
        manager.evictByPhone("13000000001");
        UserSession second = manager.createSession(user, tenant);

        // first 已被踢
        assertNull(manager.getSession(first.getSessionId()));
        // second 在
        assertNotNull(manager.getSession(second.getSessionId()));
        // tenant 内存活 session 数 = 1
        assertEquals(1, manager.listActiveSessionsByTenant(10L).size());
    }

    @Test
    void evictOldestForTenant_keeps_below_limit() throws InterruptedException {
        Tenant tenant = newTenant(10L, "T1");

        // 创建 3 个不同 phone 的 session
        UserSession s1 = manager.createSession(newUser(1L, "13000000001"), tenant);
        Thread.sleep(2); // 保证 createdAt 不同
        UserSession s2 = manager.createSession(newUser(2L, "13000000002"), tenant);
        Thread.sleep(2);
        UserSession s3 = manager.createSession(newUser(3L, "13000000003"), tenant);

        assertEquals(3, manager.listActiveSessionsByTenant(10L).size());

        // 套餐限 3 个并发：新登录前应裁剪到 < 3，即留 2
        manager.evictOldestForTenant(10L, 3);

        List<UserSession> remaining = manager.listActiveSessionsByTenant(10L);
        assertEquals(2, remaining.size());
        // 最早的 s1 应被裁
        assertNull(manager.getSession(s1.getSessionId()));
        assertNotNull(manager.getSession(s2.getSessionId()));
        assertNotNull(manager.getSession(s3.getSessionId()));
    }

    @Test
    void superAdmin_not_in_tenantIndex() {
        User admin = newUser(999L, "13003629527");
        admin.setSuperAdmin(true);

        UserSession s = manager.createSession(admin, null);

        assertNotNull(manager.getSession(s.getSessionId()));
        // 超管不挂在任何 tenantId 下；用一个随便的 id 查应为空
        assertTrue(manager.listActiveSessionsByTenant(10L).isEmpty());
    }

    @Test
    void evictByTenantId_removes_all() {
        Tenant tenant = newTenant(10L, "T1");
        UserSession s1 = manager.createSession(newUser(1L, "13000000001"), tenant);
        UserSession s2 = manager.createSession(newUser(2L, "13000000002"), tenant);

        manager.evictByTenantId(10L);

        assertNull(manager.getSession(s1.getSessionId()));
        assertNull(manager.getSession(s2.getSessionId()));
        assertTrue(manager.listActiveSessionsByTenant(10L).isEmpty());
    }

    // --- helpers ---

    private static User newUser(Long id, String phone) {
        User u = new User();
        u.setId(id);
        u.setPhone(phone);
        u.setName("user-" + id);
        u.setPassword("pwd");
        u.setStatus(User.UserStatus.ACTIVE);
        return u;
    }

    private static Tenant newTenant(Long id, String name) {
        Tenant t = new Tenant();
        t.setId(id);
        t.setName(name);
        t.setCode("CODE-" + id);
        t.setAddress("addr");
        t.setStatus(Tenant.TenantStatus.ACTIVE);
        t.setExpiresAt(LocalDateTime.now().plusYears(1));
        return t;
    }
}
