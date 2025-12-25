package com.shaxian.biz.appservice.tenant;

import com.shaxian.biz.dto.tenant.request.CreateTenantRequest;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.UserTenant;
import com.shaxian.biz.service.tenant.TenantService;
import com.shaxian.biz.service.user.UserTenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantAppService {

    private final TenantService tenantService;
    private final UserTenantService userTenantService;

    public TenantAppService(TenantService tenantService, UserTenantService userTenantService) {
        this.tenantService = tenantService;
        this.userTenantService = userTenantService;
    }

    @Transactional
    public Tenant createTenant(CreateTenantRequest request, Long userId) {
        // 创建租户实体
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setAddress(request.getAddress());
        // code 和 expiresAt 由 TenantService 自动设置

        // 创建租户
        Tenant savedTenant = tenantService.create(tenant);

        // 使用领域服务统一处理用户关联租户的逻辑
        // 创建用户租户关联关系，设置为拥有者
        // 如果用户还没有默认租户，则将新创建的租户设置为默认租户
        userTenantService.associateUserWithTenant(userId, savedTenant.getId(), 
                UserTenant.RelationshipType.OWNER, false);

        return savedTenant;
    }
}

