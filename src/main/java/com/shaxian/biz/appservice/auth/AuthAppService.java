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
import com.shaxian.crm.entity.CrmCustomer;
import com.shaxian.crm.repository.CrmCustomerRepository;
import com.shaxian.crm.service.CrmCustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthAppService {

    private final AuthService authService;
    private final BizUserSessionManager bizUserSessionManager;
    private final UserService userService;
    private final UserTenantRepository userTenantRepository;
    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final CrmCustomerService crmCustomerService;
    private final UserTenantService userTenantService;
    private final CrmCustomerRepository crmCustomerRepository;

    public AuthAppService(AuthService authService, BizUserSessionManager bizUserSessionManager,
                          UserService userService, UserTenantRepository userTenantRepository,
                          TenantRepository tenantRepository, UserRepository userRepository,
                          CrmCustomerService crmCustomerService, UserTenantService userTenantService,
                          CrmCustomerRepository crmCustomerRepository) {
        this.authService = authService;
        this.bizUserSessionManager = bizUserSessionManager;
        this.userService = userService;
        this.userTenantRepository = userTenantRepository;
        this.tenantRepository = tenantRepository;
        this.userRepository = userRepository;
        this.crmCustomerService = crmCustomerService;
        this.userTenantService = userTenantService;
        this.crmCustomerRepository = crmCustomerRepository;
    }

    /**
     * 处理登录流程，返回前端需要的用户信息结构（包含 sessionId 和租户信息）
     */
    @Transactional
    public UserSession login(String phone, String password) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        AuthService.LoginResult loginResult = authService.login(phone, password);
        User user = loginResult.getUser();
        Tenant tenant = loginResult.getTenant();

        // 如果用户没有关联租户，尝试通过手机号匹配CRM客户来关联租户
        if (tenant == null && user.getEmployeeId() == null) {
            // 通过手机号查找CRM客户
            crmCustomerRepository.findByPhone(phone).ifPresent(crmCustomer -> {
                // 如果找到CRM客户，查找该客户关联的租户
                tenantRepository.findByCrmCustomerId(crmCustomer.getId()).ifPresent(foundTenant -> {
                    // 如果找到租户，检查是否已关联，如果没有则创建关联
                    if (!userTenantService.isUserAssociatedWithTenant(user.getId(), foundTenant.getId())) {
                        // 关联用户与租户，设置为默认租户
                        userTenantService.associateUserWithTenant(
                                user.getId(),
                                foundTenant.getId(),
                                UserTenant.RelationshipType.MEMBER,
                                true  // 设置为默认租户
                        );
                    }
                });
            });
            // 重新查找默认租户（无论是否新关联，都需要重新查找以确保获取最新的默认租户）
            tenant = userService.findAndSetDefaultTenant(user.getId()).orElse(null);
        }

        // 创建用户会话
        UserSession userSession = bizUserSessionManager.createSession(user, tenant);

        // 返回包含 sessionId 的用户信息
        return userSession;
    }

    /**
     * 切换租户
     */
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

        // 验证用户是否关联该租户
        userTenantRepository.findByUserIdAndTenantId(userSession.getUserId(), tenantId)
                .orElseThrow(() -> new IllegalArgumentException("用户未关联该租户"));

        // 获取租户信息
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));

        // 更新UserSession的租户信息
        userSession.setTenantId(tenantId);
        userSession.setTenantName(tenant.getName());

        // 返回更新后的用户信息
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

    /**
     * 获取用户关联的所有租户
     */
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

    /**
     * 处理登出流程，移除用户会话
     */
    public void logout(String sessionId) {
        if (sessionId != null && !sessionId.isEmpty()) {
            bizUserSessionManager.removeSession(sessionId);
        }
    }

    /**
     * 处理注册流程，创建新用户并关联租户
     */
    @Transactional
    public Map<String, Object> register(String phone, String password, String tenantCode) {
        if (phone == null || password == null || phone.trim().isEmpty() || password.trim().isEmpty()) {
            throw new IllegalArgumentException("手机号和密码不能为空");
        }

        // 检查手机号是否已存在
        if (userRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("手机号已存在，不允许重复注册");
        }

        Tenant tenant = null;
        // 如果提供了租户代码，验证租户是否存在
        if (tenantCode != null && !tenantCode.trim().isEmpty()) {
            tenant = tenantRepository.findByCode(tenantCode)
                    .orElseThrow(() -> new IllegalArgumentException("租户代码不存在，注册失败"));
        }

        // 创建用户
        User user = new User();
        user.setPhone(phone);
        user.setPassword(password);
        user.setStatus(User.UserStatus.ACTIVE);
        user = userRepository.save(user);

        // 同步创建CRM客户记录，标记为潜在客户
        crmCustomerService.createPotentialCustomerForRegistration(phone, user.getName());

        // 如果提供了租户代码，创建用户租户关联
        if (tenant != null) {
            // 使用领域服务统一处理用户关联租户的逻辑
            userTenantService.associateUserWithTenant(user.getId(), tenant.getId(), UserTenant.RelationshipType.MEMBER, true); // 注册时关联的租户设为默认租户

            // 创建用户会话
            UserSession userSession = bizUserSessionManager.createSession(user, tenant);

            // 返回包含 sessionId 的用户信息
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
        } else {
            // 如果没有租户，只返回用户基本信息（不创建会话）
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
    }
}

