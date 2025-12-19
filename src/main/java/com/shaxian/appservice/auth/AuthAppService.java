package com.shaxian.appservice.auth;

import com.shaxian.auth.UserSession;
import com.shaxian.auth.UserSessionManager;
import com.shaxian.entity.Employee;
import com.shaxian.service.auth.AuthService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthAppService {

    private final AuthService authService;
    private final UserSessionManager userSessionManager;

    public AuthAppService(AuthService authService, UserSessionManager userSessionManager) {
        this.authService = authService;
        this.userSessionManager = userSessionManager;
    }

    /**
     * 处理登录流程，返回前端需要的用户信息结构（包含 sessionId）
     */
    public Map<String, Object> login(String phone, String password) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        Employee employee = authService.login(phone, password);

        // 创建用户会话
        UserSession userSession = userSessionManager.createSession(employee);

        // 返回包含 sessionId 的用户信息
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sessionId", userSession.getSessionId());
        userInfo.put("id", userSession.getUserId());
        userInfo.put("name", userSession.getUsername());
        userInfo.put("phone", userSession.getPhone());
        userInfo.put("email", userSession.getEmail());
        userInfo.put("role", userSession.getRole());
        userInfo.put("position", userSession.getPosition());

        return userInfo;
    }

    /**
     * 处理登出流程，移除用户会话
     */
    public void logout(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            userSessionManager.removeSession(sessionId);
        }
    }
}

