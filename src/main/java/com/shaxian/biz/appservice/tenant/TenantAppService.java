package com.shaxian.biz.appservice.tenant;

import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.dto.tenant.request.CreateTenantRequest;
import com.shaxian.biz.dto.tenant.request.TenantQueryRequest;
import com.shaxian.biz.dto.tenant.request.UpdateTenantRequest;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.UserTenant;
import com.shaxian.biz.service.tenant.TenantService;
import com.shaxian.biz.service.user.UserTenantService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setAddress(request.getAddress());
        // code 和 expiresAt 由 TenantService 自动设置

        Tenant savedTenant = tenantService.create(tenant);

        if (userId != null) {
            userTenantService.associateUserWithTenant(userId, savedTenant.getId(),
                    UserTenant.RelationshipType.OWNER, false);
        }

        return savedTenant;
    }

    public PageResult<Tenant> queryTenants(TenantQueryRequest request, Integer pageNo, Integer pageSize) {
        Page<Tenant> page = tenantService.queryTenants(
                request.getName(),
                request.getCode(),
                request.getStatus(),
                pageNo,
                pageSize
        );
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNo, pageSize);
    }

    public Optional<Tenant> getTenant(Long id) {
        return tenantService.getById(id);
    }

    @Transactional
    public Tenant updateTenant(Long id, UpdateTenantRequest request) {
        Tenant tenant = tenantService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));

        if (request.getName() != null) {
            tenant.setName(request.getName());
        }
        if (request.getAddress() != null) {
            tenant.setAddress(request.getAddress());
        }
        if (request.getStatus() != null) {
            try {
                tenant.setStatus(Tenant.TenantStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的状态值: " + request.getStatus());
            }
        }

        return tenantService.update(id, tenant);
    }
}

