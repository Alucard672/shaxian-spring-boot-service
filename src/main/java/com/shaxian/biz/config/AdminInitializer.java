package com.shaxian.biz.config;

import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import com.shaxian.biz.entity.UserTenant;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.repository.UserRepository;
import com.shaxian.biz.service.user.UserTenantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    private static final String PLATFORM_TENANT_CODE = "PLATFORM";
    private static final String ADMIN_PHONE = "13003629527";
    private static final String ADMIN_DEFAULT_PASSWORD = "admin123456";
    private static final String ADMIN_NAME = "超级管理员";

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final UserTenantService userTenantService;

    public AdminInitializer(UserRepository userRepository,
                            TenantRepository tenantRepository,
                            UserTenantService userTenantService) {
        this.userRepository = userRepository;
        this.tenantRepository = tenantRepository;
        this.userTenantService = userTenantService;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Tenant platformTenant = tenantRepository.findByCode(PLATFORM_TENANT_CODE)
                .orElseGet(() -> {
                    Tenant t = new Tenant();
                    t.setName("平台管理");
                    t.setCode(PLATFORM_TENANT_CODE);
                    t.setAddress("");
                    t.setStatus(Tenant.TenantStatus.ACTIVE);
                    return tenantRepository.save(t);
                });

        if (userRepository.existsByPhone(ADMIN_PHONE)) {
            return;
        }

        User admin = new User();
        admin.setPhone(ADMIN_PHONE);
        admin.setName(ADMIN_NAME);
        admin.setPassword(ADMIN_DEFAULT_PASSWORD);
        admin.setStatus(User.UserStatus.ACTIVE);
        admin = userRepository.save(admin);

        userTenantService.associateUserWithTenant(
                admin.getId(), platformTenant.getId(),
                UserTenant.RelationshipType.OWNER, true);

        log.info("超级管理员账号已初始化，手机号：{}，请登录后立即修改密码", ADMIN_PHONE);
    }
}
