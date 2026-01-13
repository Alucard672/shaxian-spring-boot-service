package com.shaxian.crm.auth;

import com.shaxian.biz.api.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * CRM会话认证拦截器
 * 统一校验 sessionId，并将 CrmUserSession 存入请求属性
 */
@Component
public class CrmSessionAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CrmSessionAuthInterceptor.class);
    private static final String SESSION_ID_HEADER = "X-Session-Id";
    private static final String SESSION_ID_PARAM = "sessionId";
    private static final String CURRENT_CRM_USER_SESSION = "CURRENT_CRM_USER_SESSION";

    private final CrmUserSessionManager crmUserSessionManager;
    private final ObjectMapper objectMapper;

    public CrmSessionAuthInterceptor(CrmUserSessionManager crmUserSessionManager, ObjectMapper objectMapper) {
        this.crmUserSessionManager = crmUserSessionManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 Header 或参数中获取 sessionId
        String sessionId = getSessionId(request);

        if (sessionId == null || sessionId.isEmpty()) {
            logger.warn("CRM请求缺少 sessionId: {}", request.getRequestURI());
            writeErrorResponse(response, "缺少 sessionId");
            return false;
        }

        // 从会话管理器获取 CrmUserSession
        CrmUserSession crmUserSession = crmUserSessionManager.getSession(sessionId);
        if (crmUserSession == null) {
            logger.warn("无效的 CRM sessionId: {}", sessionId);
            writeErrorResponse(response, "无效的 sessionId");
            return false;
        }

        // 将 CrmUserSession 存入请求属性，供后续参数解析器和业务层使用
        request.setAttribute(CURRENT_CRM_USER_SESSION, crmUserSession);

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
