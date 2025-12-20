package com.shaxian.auth;

import com.shaxian.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 会话认证拦截器
 * 统一校验 sessionId，并将 UserSession 存入请求属性
 */
@Component
public class SessionAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(SessionAuthInterceptor.class);
    private static final String SESSION_ID_HEADER = "X-Session-Id";
    private static final String SESSION_ID_PARAM = "sessionId";
    private static final String CURRENT_USER_SESSION = "CURRENT_USER_SESSION";

    private final UserSessionManager userSessionManager;
    private final ObjectMapper objectMapper;

    public SessionAuthInterceptor(UserSessionManager userSessionManager, ObjectMapper objectMapper) {
        this.userSessionManager = userSessionManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 Header 或参数中获取 sessionId
        String sessionId = getSessionId(request);

        if (sessionId == null || sessionId.isEmpty()) {
            logger.warn("请求缺少 sessionId: {}", request.getRequestURI());
            writeErrorResponse(response, "缺少 sessionId");
            return false;
        }

        // 从会话管理器获取 UserSession
        UserSession userSession = userSessionManager.getSession(sessionId);
        if (userSession == null) {
            logger.warn("无效的 sessionId: {}", sessionId);
            writeErrorResponse(response, "无效的 sessionId");
            return false;
        }

        // 验证租户ID不为null
        if (userSession.getTenantId() == null) {
            logger.warn("用户会话缺少租户信息: {}", sessionId);
            writeErrorResponse(response, "用户会话缺少租户信息");
            return false;
        }

        // 将 UserSession 存入请求属性，供后续参数解析器和业务层使用
        request.setAttribute(CURRENT_USER_SESSION, userSession);

        return true;
    }

    /**
     * 从请求中获取 sessionId
     * 优先从 Header 读取，若无则从 Query/Form 参数读取
     */
    private String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader(SESSION_ID_HEADER);
        if (sessionId == null || sessionId.isEmpty()) {
            sessionId = request.getParameter(SESSION_ID_PARAM);
        }
        return sessionId;
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> errorResponse = ApiResponse.fail(message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
