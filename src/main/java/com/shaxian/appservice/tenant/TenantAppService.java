package com.shaxian.appservice.tenant;

import com.shaxian.dto.tenant.request.CreateTenantRequest;
import com.shaxian.entity.Tenant;
import com.shaxian.entity.UserTenant;
import com.shaxian.repository.UserTenantRepository;
import com.shaxian.service.tenant.TenantService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TenantAppService {

    private final TenantService tenantService;
    private final UserTenantRepository userTenantRepository;

    public TenantAppService(TenantService tenantService, UserTenantRepository userTenantRepository) {
        this.tenantService = tenantService;
        this.userTenantRepository = userTenantRepository;
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

        // 创建用户租户关联关系，设置为拥有者
        UserTenant userTenant = new UserTenant();
        userTenant.setUserId(userId);
        userTenant.setTenantId(savedTenant.getId());
        userTenant.setRelationshipType(UserTenant.RelationshipType.OWNER);
        
        // 如果用户还没有默认租户，则将新创建的租户设置为默认租户
        // 这样用户重新登录时就能在会话信息中找到企业信息
        boolean hasDefaultTenant = userTenantRepository.findByUserIdAndIsDefaultTrue(userId).isPresent();
        userTenant.setIsDefault(!hasDefaultTenant);

        userTenantRepository.save(userTenant);

        return savedTenant;
    }
}

