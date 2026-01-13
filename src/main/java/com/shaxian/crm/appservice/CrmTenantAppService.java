package com.shaxian.crm.appservice;

import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.service.tenant.TenantService;
import com.shaxian.crm.dto.request.CrmTenantQueryRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class CrmTenantAppService {

    private final TenantService tenantService;

    public CrmTenantAppService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    /**
     * 查询租户列表
     */
    public PageResult<Tenant> queryTenants(CrmTenantQueryRequest request, Integer pageNo, Integer pageSize) {
        Page<Tenant> page = tenantService.queryTenants(
                request.getName(),
                request.getCode(),
                request.getStatus(),
                pageNo,
                pageSize
        );
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNo, pageSize);
    }

    /**
     * 启用租户
     */
    public Tenant activateTenant(Long id) {
        return tenantService.activate(id);
    }

    /**
     * 停用租户
     */
    public Tenant deactivateTenant(Long id) {
        return tenantService.deactivate(id);
    }
}
