package com.shaxian.crm.appservice;

import com.shaxian.biz.api.PageResult;
import com.shaxian.crm.dto.request.CreateCrmUserRequest;
import com.shaxian.crm.dto.request.CrmUserQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmUserRequest;
import com.shaxian.crm.entity.CrmUserInfo;
import com.shaxian.crm.service.CrmUserService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CrmUserAppService {

    private final CrmUserService crmUserService;

    public CrmUserAppService(CrmUserService crmUserService) {
        this.crmUserService = crmUserService;
    }

    /**
     * 查询用户列表
     */
    public PageResult<CrmUserInfo> queryUsers(CrmUserQueryRequest request, Integer pageNo, Integer pageSize) {
        Page<CrmUserInfo> page = crmUserService.queryUsers(
                request.getStatus(),
                request.getPhone(),
                request.getName(),
                request.getEmail(),
                pageNo,
                pageSize
        );
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNo, pageSize);
    }

    /**
     * 根据ID获取用户
     */
    public Optional<CrmUserInfo> findUser(Long id) {
        return crmUserService.getById(id);
    }

    /**
     * 创建用户
     */
    public CrmUserInfo createUser(CreateCrmUserRequest request) {
        // 校验角色ID列表
        if (request.getRoleIds() == null || request.getRoleIds().isEmpty()) {
            throw new IllegalArgumentException("用户必须至少有一个角色");
        }
        return crmUserService.create(
                request.getPhone(),
                request.getName(),
                request.getEmail(),
                request.getRoleIds()
        );
    }

    /**
     * 更新用户
     */
    public CrmUserInfo updateUser(Long id, UpdateCrmUserRequest request) {
        // 如果提供了角色ID列表，需要校验
        if (request.getRoleIds() != null && request.getRoleIds().isEmpty()) {
            throw new IllegalArgumentException("用户必须至少有一个角色");
        }
        return crmUserService.update(
                id,
                request.getPhone(),
                request.getName(),
                request.getEmail(),
                request.getRoleIds()
        );
    }

    /**
     * 更新用户状态
     */
    public void updateUserStatus(Long id, CrmUserInfo.UserStatus status) {
        crmUserService.updateStatus(id, status);
    }
}

