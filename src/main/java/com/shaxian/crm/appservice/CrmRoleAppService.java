package com.shaxian.crm.appservice;

import com.shaxian.biz.api.PageResult;
import com.shaxian.crm.dto.request.CreateCrmRoleRequest;
import com.shaxian.crm.dto.request.CrmRoleQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmRoleRequest;
import com.shaxian.crm.entity.CrmRole;
import com.shaxian.crm.service.CrmRoleService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrmRoleAppService {

    private final CrmRoleService crmRoleService;

    public CrmRoleAppService(CrmRoleService crmRoleService) {
        this.crmRoleService = crmRoleService;
    }

    /**
     * 查询角色列表
     */
    public PageResult<CrmRole> queryRoles(CrmRoleQueryRequest request, Integer pageNo, Integer pageSize) {
        Page<CrmRole> page = crmRoleService.queryRoles(
                request.getName(),
                request.getCode(),
                request.getStatus(),
                pageNo,
                pageSize
        );
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNo, pageSize);
    }

    /**
     * 根据ID获取角色
     */
    public Optional<CrmRole> findRole(Long id) {
        return crmRoleService.getById(id);
    }

    /**
     * 创建角色
     */
    public CrmRole createRole(CreateCrmRoleRequest request) {
        CrmRole role = new CrmRole();
        role.setName(request.getName());
        role.setCode(request.getCode());
        role.setLevel(request.getLevel() != null ? request.getLevel() : 0);
        role.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            role.setStatus(CrmRole.RoleStatus.valueOf(request.getStatus()));
        }
        return crmRoleService.create(role);
    }

    /**
     * 更新角色
     */
    public CrmRole updateRole(Long id, UpdateCrmRoleRequest request) {
        CrmRole role = new CrmRole();
        if (request.getName() != null) role.setName(request.getName());
        if (request.getCode() != null) role.setCode(request.getCode());
        if (request.getLevel() != null) role.setLevel(request.getLevel());
        if (request.getStatus() != null) {
            role.setStatus(CrmRole.RoleStatus.valueOf(request.getStatus()));
        }
        if (request.getDescription() != null) role.setDescription(request.getDescription());
        return crmRoleService.update(id, role);
    }

    /**
     * 更新角色状态
     */
    public void updateRoleStatus(Long id, CrmRole.RoleStatus status) {
        crmRoleService.updateStatus(id, status);
    }

    /**
     * 根据ID列表批量获取角色
     */
    public List<CrmRole> getRolesByIds(List<Long> ids) {
        return crmRoleService.getRolesByIds(ids);
    }
}
