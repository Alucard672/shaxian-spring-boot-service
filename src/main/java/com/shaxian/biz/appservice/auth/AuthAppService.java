package com.shaxian.biz.appservice.auth;

import com.shaxian.biz.auth.BizUserSessionManager;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import com.shaxian.biz.entity.UserTenant;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.repository.UserRepository;
import com.shaxian.biz.repository.UserTenantRepository;
import com.shaxian.biz.service.auth.AuthService;
import com.shaxian.biz.service.user.UserService;
import com.shaxian.biz.service.user.UserTenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AuthAppService {

    private final AuthService authService;
    private final BizUserSessionManager bizUserSessionManager;
    private final UserService userService;
    private final UserTenantRepository userTenantRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final UserTenantService userTenantService;

    public AuthAppService(AuthService authService, BizUserSessionManager bizUserSessionManager,
                          UserService userService, UserTenantRepository userTenantRepository,
                          TenantRepository tenantRepository, UserRepository userRepository,
                          UserTenantService userTenantService) {
        this.authService = authService;
        this.bizUserSessionManager = bizUserSessionManager;
        this.userService = userService;
        this.userTenantRepository = userTenantRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.userTenantService = userTenantService;
    }

    @Transactional
    public UserSession login(String phone, String password) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            if (!"123456".equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        } else {
            if (!user.getPassword().equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        Tenant tenant = userService.findAndSetDefaultTenant(user.getId()).orElse(null);

        // 业务用户（非员工用户）必须关联租户才能登录
        if (user.getEmployeeId() == null && tenant == null) {
            throw new IllegalArgumentException("业务用户必须关联租户才能登录");
        }

        return bizUserSessionManager.createSession(user, tenant);
    }

    public Map<String, Object> switchTenant(String sessionId, Long tenantId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }
        if (tenantId == null) {
            throw new IllegalArgumentException("tenantId不能为空");
        }

        UserSession userSession = bizUserSessionManager.getSession(sessionId);
        if (userSession == null) {
            throw new IllegalArgumentException("无效的sessionId");
        }

        userTenantRepository.findByUserIdAndTenantId(userSession.getUserId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("用户未关联该租户"));

        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));

        userSession.setTenantId(tenantId);
        userSession.setTenantName(tenant.getName());

        return buildUserInfo(userSession);
    }

    public List<UserTenant> getUserTenants(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            throw new IllegalArgumentException("sessionId不能为空");
        }

        UserSession userSession = bizUserSessionManager.getSession(sessionId);
        if (userSession == null) {
            throw new IllegalArgumentException("无效的sessionId");
        }

        return userService.getUserTenants(userSession.getUserId());
    }

    public void logout(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            bizUserSessionManager.removeSession(sessionId);
        }
    }

    @Transactional
    public Map<String, Object> register(String phone, String password, String tenantCode) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("手机号已存在，不允许重复注册");
        }

        Tenant tenant = null;
        if (tenantCode != null && !tenantCode.trim().isEmpty()) {
            tenant = tenantRepository.findByCode(tenantCode)
                    .orElseThrow(() -> new IllegalArgumentException("租户代码不存在，注册失败"));
        }

        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setStatus(User.UserStatus.ACTIVE);
        user = userRepository.save(user);

        if (tenant != null) {
            userTenantService.associateUserWithTenant(user.getId(), tenant.getId(),
                    UserTenant.RelationshipType.MEMBER, true);

            UserSession userSession = bizUserSessionManager.createSession(user, tenant);
            return buildUserInfo(userSession);
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("name", user.getName());
        userInfo.put("phone", user.getPhone());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", null);
        userInfo.put("position", null);
        userInfo.put("tenantId", null);
        userInfo.put("tenantName", null);
        return userInfo;
    }

    private Map<String, Object> buildUserInfo(UserSession userSession) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("sessionId", userSession.getSessionId());
        userInfo.put("id", userSession.getUserId());
        userInfo.put("name", userSession.getUsername());
        userInfo.put("phone", userSession.getPhone());
        userInfo.put("email", userSession.getEmail());
        userInfo.put("role", userSession.getRole());
        userInfo.put("position", userSession.getPosition());
        userInfo.put("tenantId", userSession.getTenantId());
        userInfo.put("tenantName", userSession.getTenantName());
        return userInfo;
    }
}
