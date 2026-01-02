package com.shaxian.crm.appservice;

import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.auth.UserSessionManager;
import com.shaxian.crm.entity.CrmUserInfo;
import com.shaxian.crm.service.CrmAuthService;
import org.springframework.stereotype.Service;

@Service
public class CrmAuthAppService {

    private final CrmAuthService crmAuthService;
    private final UserSessionManager userSessionManager;

    public CrmAuthAppService(CrmAuthService crmAuthService, UserSessionManager userSessionManager) {
        this.crmAuthService = crmAuthService;
        this.userSessionManager = userSessionManager;
    }

    /**
     * CRM用户登录
     * 验证用户信息并创建会话
     */
    public UserSession login(String phone, String password) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        // 验证CRM用户
        CrmUserInfo crmUser = crmAuthService.login(phone, password);

        // 创建UserSession（CRM用户没有租户，tenant为null）
        String sessionId = java.util.UUID.randomUUID().toString();
        UserSession userSession = new UserSession(
                sessionId,
                crmUser.getId(),
                crmUser.getName(),
                crmUser.getPhone(),
                crmUser.getEmail(),
                null, // role
                null, // position
                null, // tenantId - CRM用户没有租户
                null, // tenantName
                null  // tenantCode
        );

        // 使用UserSessionManager存储会话
        return userSessionManager.createSession(userSession);
    }

    /**
     * CRM用户登出
     */
    public void logout(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            userSessionManager.removeSession(sessionId);
        }
    }
}

