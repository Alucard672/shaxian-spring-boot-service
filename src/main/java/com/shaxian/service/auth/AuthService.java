package com.shaxian.service.auth;

import com.shaxian.entity.Tenant;
import com.shaxian.entity.User;
import com.shaxian.repository.UserRepository;
import com.shaxian.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;

    public AuthService(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * 登录校验，返回通过校验的用户和默认租户信息（租户可能为null）
     */
    public LoginResult login(String phone, String password) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 检查密码（默认密码123456）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            if (!"123456".equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        } else {
            if (!user.getPassword().equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        }

        // 检查用户状态
        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        // 查找用户的默认租户（允许为null，用户可以在登录后创建企业）
        Tenant defaultTenant = userService.getDefaultTenant(user.getId()).orElse(null);

        return new LoginResult(user, defaultTenant);
    }

    /**
     * 登录结果
     */
    public static class LoginResult {
        private final User user;
        private final Tenant tenant;

        public LoginResult(User user, Tenant tenant) {
            this.user = user;
            this.tenant = tenant;
        }

        public User getUser() {
            return user;
        }

        public Tenant getTenant() {
            return tenant;
        }
    }
}

