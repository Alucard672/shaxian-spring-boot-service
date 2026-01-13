package com.shaxian.crm.appservice;

import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.auth.CrmUserSessionManager;
import com.shaxian.crm.entity.CrmRole;
import com.shaxian.crm.entity.CrmUserInfo;
import com.shaxian.crm.service.CrmAuthService;
import com.shaxian.crm.service.CrmRoleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CrmAuthAppService {

    private final CrmAuthService crmAuthService;
    private final CrmUserSessionManager crmUserSessionManager;
    private final CrmRoleService crmRoleService;

    public CrmAuthAppService(CrmAuthService crmAuthService, CrmUserSessionManager crmUserSessionManager, CrmRoleService crmRoleService) {
        this.crmAuthService = crmAuthService;
        this.crmUserSessionManager = crmUserSessionManager;
        this.crmRoleService = crmRoleService;
    }

    /**
     * CRM用户登录
     * 验证用户信息并创建会话
     */
    public CrmUserSession login(String phone, String password) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        // 验证CRM用户
        CrmUserInfo crmUser = crmAuthService.login(phone, password);

        // 获取用户角色信息
        List<Long> roleIds = crmUser.getRoleIdsList();
        List<CrmRole> roles = new ArrayList<>();
        List<String> roleNames = new ArrayList<>();
        List<String> roleCodes = new ArrayList<>();

        if (roleIds != null && !roleIds.isEmpty()) {
            roles = crmRoleService.getRolesByIds(roleIds);
            roleNames = roles.stream().map(CrmRole::getName).collect(Collectors.toList());
            roleCodes = roles.stream().map(CrmRole::getCode).collect(Collectors.toList());
        }

        // 创建CrmUserSession
        String sessionId = java.util.UUID.randomUUID().toString();
        CrmUserSession crmUserSession = new CrmUserSession(
                sessionId,
                crmUser.getId(),
                crmUser.getName(),
                crmUser.getPhone(),
                crmUser.getEmail()
        );
        crmUserSession.setRoleIds(roleIds != null ? roleIds : new ArrayList<>());
        crmUserSession.setRoleNames(roleNames);
        crmUserSession.setRoleCodes(roleCodes);

        // 使用CrmUserSessionManager存储会话
        return crmUserSessionManager.createSession(crmUserSession);
    }

    /**
     * CRM用户登出
     */
    public void logout(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            crmUserSessionManager.removeSession(sessionId);
        }
    }

    /**
     * 加载CRM用户会话
     * 根据sessionId获取会话信息，如果不存在则抛出异常
     *
     * @param sessionId 会话ID
     * @return CRM用户会话对象
     * @throws IllegalArgumentException 如果sessionId为空或会话不存在
     */
    public CrmUserSession loadSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }

        CrmUserSession crmUserSession = crmUserSessionManager.getSession(sessionId);
        if (crmUserSession == null) {
            throw new IllegalArgumentException("会话不存在或已过期");
        }

        // 预留：如果后续需要刷新session过期时间，可以在这里调用刷新方法
        // 例如：crmUserSessionManager.refreshSession(sessionId);

        return crmUserSession;
    }
}

