package com.shaxian.biz.auth;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.util.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

/**
 * BIZ会话认证拦截器
 * 统一校验 sessionId，并将 UserSession 存入请求属性
 */
@Component
public class BizSessionAuthInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(BizSessionAuthInterceptor.class);
    private static final String SESSION_ID_HEADER = "X-Session-Id";
    private static final String SESSION_ID_PARAM = "sessionId";
    private static final String CURRENT_USER_SESSION = "CURRENT_USER_SESSION";
    private static final String ADMIN_URI_PREFIX = "/biz/api/admin/";

    private final BizUserSessionManager bizUserSessionManager;
    private final ObjectMapper objectMapper;

    public BizSessionAuthInterceptor(BizUserSessionManager bizUserSessionManager, ObjectMapper objectMapper) {
        this.bizUserSessionManager = bizUserSessionManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从 Header 或参数中获取 sessionId
        String sessionId = getSessionId(request);

        if (sessionId == null || sessionId.isEmpty()) {
            logger.warn("请求缺少 sessionId: {}", request.getRequestURI());
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "缺少 sessionId");
            return false;
        }

        // 从会话管理器获取 UserSession
        UserSession userSession = bizUserSessionManager.getSession(sessionId);
        if (userSession == null) {
            logger.warn("无效的 sessionId: {}", sessionId);
            writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "无效的 sessionId");
            return false;
        }

        // 租户状态/到期校验（用 session 内快照，避免每请求查 DB；AC-7, AC-13）
        if (userSession.getTenantId() != null) {
            if (userSession.getTenantStatus() != null
                    && userSession.getTenantStatus() != Tenant.TenantStatus.ACTIVE) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "租户已停用");
                return false;
            }
            if (userSession.getTenantExpiresAt() != null
                    && userSession.getTenantExpiresAt().isBefore(LocalDateTime.now())) {
                writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "租户已到期");
                return false;
            }
        }

        // 管理端域权限校验（AC-16）
        if (request.getRequestURI().startsWith(ADMIN_URI_PREFIX)) {
            if (!userSession.isSuperAdmin()) {
                writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, "无管理后台权限");
                return false;
            }
        }

        // 将租户ID设置到ThreadLocal中，供Hibernate多租户使用
        TenantContext.setTenantId(userSession.getTenantId());

        // 将 UserSession 存入请求属性
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
     * 请求处理完成后清理ThreadLocal，避免内存泄漏
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        TenantContext.clear();
    }

    /**
     * 写入错误响应
     */
    private void writeErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ApiResponse<Void> errorResponse = ApiResponse.fail(message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
