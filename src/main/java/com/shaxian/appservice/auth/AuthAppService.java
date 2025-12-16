package com.shaxian.appservice.auth;

import com.shaxian.entity.Employee;
import com.shaxian.service.auth.AuthService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthAppService {

    private final AuthService authService;

    public AuthAppService(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 处理登录流程，返回前端需要的用户信息结构
     */
    public Map<String, Object> login(String phone, String password) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        Employee employee = authService.login(phone, password);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", employee.getId());
        userInfo.put("name", employee.getName());
        userInfo.put("phone", employee.getPhone());
        userInfo.put("email", employee.getEmail());
        userInfo.put("role", employee.getRole());
        userInfo.put("position", employee.getPosition());

        return userInfo;
    }
}

