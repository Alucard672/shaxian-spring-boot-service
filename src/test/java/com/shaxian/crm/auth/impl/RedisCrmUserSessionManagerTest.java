package com.shaxian.crm.auth.impl;

import com.shaxian.crm.auth.CrmUserSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * RedisCrmUserSessionManager 测试类
 */
class RedisCrmUserSessionManagerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    private RedisCrmUserSessionManager sessionManager;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        sessionManager = new RedisCrmUserSessionManager();
        // 使用反射设置redisTemplate
        try {
            java.lang.reflect.Field field = RedisCrmUserSessionManager.class.getDeclaredField("redisTemplate");
            field.setAccessible(true);
            field.set(sessionManager, redisTemplate);

            // 设置sessionTimeout
            field = RedisCrmUserSessionManager.class.getDeclaredField("sessionTimeout");
            field.setAccessible(true);
            field.set(sessionManager, 7200);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGetSession_Success() {
        // 准备数据
        String sessionId = "test-session-id";
        CrmUserSession expectedSession = createSampleSession(sessionId);

        // 模拟Redis操作
        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(redisTemplate.opsForValue().get("crm:session:" + sessionId)).thenReturn(expectedSession);

        // 执行测试
        CrmUserSession actualSession = sessionManager.getSession(sessionId);

        // 验证结果
        assertEquals(expectedSession, actualSession);
        verify(redisTemplate.opsForValue()).get("crm:session:" + sessionId);
    }

    @Test
    void testGetSession_NullSessionId() {
        // 执行测试
        CrmUserSession session = sessionManager.getSession(null);

        // 验证结果
        assertNull(session);
    }

    @Test
    void testGetSession_EmptySessionId() {
        // 执行测试
        CrmUserSession session = sessionManager.getSession("");

        // 验证结果
        assertNull(session);
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRemoveSession_Success() {
        // 准备数据
        String sessionId = "test-session-id";

        // 执行测试
        sessionManager.removeSession(sessionId);

        // 验证结果
        verify(redisTemplate).delete("crm:session:" + sessionId);
    }

    @Test
    void testRemoveSession_NullSessionId() {
        // 执行测试
        sessionManager.removeSession(null);

        // 验证结果
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    void testRemoveSession_EmptySessionId() {
        // 执行测试
        sessionManager.removeSession("");

        // 验证结果
        verify(redisTemplate, never()).delete(anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testCreateSession_Success() {
        // 准备数据
        String sessionId = "test-session-id";
        CrmUserSession sessionToCreate = createSampleSession(sessionId);

        ValueOperations<String, Object> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);

        // 执行测试
        CrmUserSession result = sessionManager.createSession(sessionToCreate);

        // 验证结果
        assertEquals(sessionToCreate, result);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Object> valueCaptor = ArgumentCaptor.forClass(Object.class);

        verify(valueOps).set(keyCaptor.capture(), valueCaptor.capture(), eq(7200L), eq(java.util.concurrent.TimeUnit.SECONDS));

        assertEquals("crm:session:" + sessionId, keyCaptor.getValue());
        assertEquals(sessionToCreate, valueCaptor.getValue());
    }

    @Test
    void testCreateSession_NullSession() {
        // 执行测试和验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            sessionManager.createSession(null);
        });
    }

    @Test
    void testCreateSession_NullSessionId() {
        // 准备数据
        CrmUserSession sessionWithoutId = new CrmUserSession();
        sessionWithoutId.setUserId(1L);
        sessionWithoutId.setUsername("test-user");

        // 执行测试和验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            sessionManager.createSession(sessionWithoutId);
        });
    }

    @Test
    void testCreateSession_EmptySessionId() {
        // 准备数据
        CrmUserSession sessionWithoutId = new CrmUserSession();
        sessionWithoutId.setSessionId("");
        sessionWithoutId.setUserId(1L);
        sessionWithoutId.setUsername("test-user");

        // 执行测试和验证异常
        assertThrows(IllegalArgumentException.class, () -> {
            sessionManager.createSession(sessionWithoutId);
        });
    }

    @Test
    @SuppressWarnings("unchecked")
    void testRefreshSession_WithValidSession() {
        // 准备数据
        String sessionId = "test-session-id";
        CrmUserSession existingSession = createSampleSession(sessionId);

        when(redisTemplate.opsForValue()).thenReturn(mock(ValueOperations.class));
        when(redisTemplate.opsForValue().get("crm:session:" + sessionId)).thenReturn(existingSession);

        // 执行测试
        sessionManager.refreshSession(sessionId);

        // 验证结果
        verify(redisTemplate).expire(eq("crm:session:" + sessionId), eq(7200L), eq(java.util.concurrent.TimeUnit.SECONDS));
    }

    @Test
    void testRefreshSession_WithNullSessionId() {
        // 执行测试
        sessionManager.refreshSession(null);

        // 验证结果
        verify(redisTemplate, never()).expire(any(), anyLong(), any());
    }

    private CrmUserSession createSampleSession(String sessionId) {
        CrmUserSession session = new CrmUserSession();
        session.setSessionId(sessionId);
        session.setUserId(1L);
        session.setUsername("test-user");
        session.setEmail("test@example.com");
        session.setPhone("1234567890");

        List<Long> roleIds = Arrays.asList(1L, 2L);
        List<String> roleNames = Arrays.asList("admin", "user");
        List<String> roleCodes = Arrays.asList("ROLE_ADMIN", "ROLE_USER");

        session.setRoleIds(roleIds);
        session.setRoleNames(roleNames);
        session.setRoleCodes(roleCodes);

        return session;
    }
}