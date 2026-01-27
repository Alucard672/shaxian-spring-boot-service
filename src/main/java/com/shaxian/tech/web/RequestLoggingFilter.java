package com.shaxian.tech.web;

import com.shaxian.biz.auth.BizUserSessionManager;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.crm.auth.CrmUserSessionManager;
import com.shaxian.crm.auth.CrmUserSession;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局请求日志过滤器
 * 记录每个请求的用户信息、租户信息和请求参数
 */
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String SESSION_ID_HEADER = "X-Session-Id";
    private static final String SESSION_ID_PARAM = "sessionId";

    private final BizUserSessionManager bizUserSessionManager;
    private final CrmUserSessionManager crmUserSessionManager;

    public RequestLoggingFilter(BizUserSessionManager bizUserSessionManager,
                                CrmUserSessionManager crmUserSessionManager) {
        this.bizUserSessionManager = bizUserSessionManager;
        this.crmUserSessionManager = crmUserSessionManager;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 使用 ContentCachingRequestWrapper 包装请求，以便可以安全地读取 body
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(httpResponse);

        try {
            // 获取请求路径
            String requestPath = wrappedRequest.getRequestURI();
            String method = wrappedRequest.getMethod();

            // 从请求中获取 sessionId
            String sessionId = getSessionId(wrappedRequest);

            // 尝试获取用户会话信息
            Long userId = null;
            Long tenantId = null;
            String sessionType = null;

            if (StringUtils.hasText(sessionId)) {
                // 先尝试从 BIZ 会话管理器获取
                UserSession bizUserSession = bizUserSessionManager.getSession(sessionId);
                if (bizUserSession != null) {
                    userId = bizUserSession.getUserId();
                    tenantId = bizUserSession.getTenantId();
                    sessionType = "BIZ";
                } else {
                    // 尝试从 CRM 会话管理器获取
                    CrmUserSession crmUserSession = crmUserSessionManager.getSession(sessionId);
                    if (crmUserSession != null) {
                        userId = crmUserSession.getUserId();
                        // CRM 会话没有租户ID，设置为 null
                        tenantId = null;
                        sessionType = "CRM";
                    }
                }
            }

            // 获取请求参数
            Map<String, String> queryParams = getQueryParams(wrappedRequest);

            // 在请求处理前记录基本信息（不包含请求体）
            logRequestBefore(method, requestPath, sessionId, userId, tenantId, sessionType, queryParams);

            // 继续处理请求（先让请求处理完成，以便可以读取 body）
            chain.doFilter(wrappedRequest, wrappedResponse);

            // 请求处理完成后，读取请求体并补充日志
            String requestBody = getRequestBody(wrappedRequest);
            if (requestBody != null && !requestBody.isEmpty()) {
                logRequestBody(requestPath, requestBody);
            }

            // 将响应内容写回客户端
            wrappedResponse.copyBodyToResponse();
        } catch (Exception e) {
            logger.error("请求日志记录失败", e);
            // 确保响应被写回
            wrappedResponse.copyBodyToResponse();
        }
    }

    /**
     * 从请求中获取 sessionId
     * 优先从 Header 读取，若无则从 Query 参数读取
     */
    private String getSessionId(HttpServletRequest request) {
        String sessionId = request.getHeader(SESSION_ID_HEADER);
        if (!StringUtils.hasText(sessionId)) {
            sessionId = request.getParameter(SESSION_ID_PARAM);
        }
        return sessionId;
    }

    /**
     * 获取查询参数
     */
    private Map<String, String> getQueryParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            params.put(paramName, paramValue);
        }
        return params;
    }

    /**
     * 获取请求体内容（仅对 POST/PUT/PATCH 请求）
     * 使用 ContentCachingRequestWrapper 可以安全地读取 body
     */
    private String getRequestBody(ContentCachingRequestWrapper request) {
        String method = request.getMethod();
        if (!"POST".equals(method) && !"PUT".equals(method) && !"PATCH".equals(method)) {
            return null;
        }

        String contentType = request.getContentType();
        if (contentType != null && contentType.contains("application/json")) {
            byte[] contentAsByteArray = request.getContentAsByteArray();
            if (contentAsByteArray.length > 0) {
                return new String(contentAsByteArray, StandardCharsets.UTF_8);
            }
        }

        return null;
    }

    /**
     * 记录请求基本信息（请求处理前）
     */
    private void logRequestBefore(String method, String requestPath, String sessionId,
                                  Long userId, Long tenantId, String sessionType,
                                  Map<String, String> queryParams) {
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("\n========== 请求日志 ==========");
        logBuilder.append("\n请求方法: ").append(method);
        logBuilder.append("\n请求路径: ").append(requestPath);
        logBuilder.append("\nSessionId: ").append(sessionId != null ? sessionId : "无");
        logBuilder.append("\n会话类型: ").append(sessionType != null ? sessionType : "无");
        logBuilder.append("\n当前用户ID: ").append(userId != null ? userId : "无");
        logBuilder.append("\n当前租户ID: ").append(tenantId != null ? tenantId : "无");

        if (!queryParams.isEmpty()) {
            logBuilder.append("\n查询参数: ").append(queryParams);
        }

        logBuilder.append("\n==============================");

        logger.info(logBuilder.toString());
    }

    /**
     * 记录请求体信息（请求处理后）
     */
    private void logRequestBody(String requestPath, String requestBody) {
        // 限制请求体日志长度，避免日志过长
        String bodyPreview = requestBody.length() > 500 
                ? requestBody.substring(0, 500) + "...(已截断)" 
                : requestBody;
        logger.info("请求路径: {} | 请求体: {}", requestPath, bodyPreview);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化方法，可以留空
    }

    @Override
    public void destroy() {
        // 销毁方法，可以留空
    }
}
