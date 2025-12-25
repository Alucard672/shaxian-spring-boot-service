package com.shaxian.biz.service.user;

import com.shaxian.biz.entity.UserTenant;
import com.shaxian.biz.repository.UserTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 用户租户关联领域服务
 * 统一封装用户关联租户的核心逻辑，支持多种场景：
 * - 用户注册时提供企业代码自动关联
 * - 企业管理员手动关联用户
 * - 创建租户时关联创建者
 * - 授权员工登录时关联租户
 */
@Service
public class UserTenantService {

    private final UserTenantRepository userTenantRepository;

    public UserTenantService(UserTenantRepository userTenantRepository) {
        this.userTenantRepository = userTenantRepository;
    }

    /**
     * 关联用户和租户
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @param relationshipType 关系类型（OWNER/MEMBER），默认为MEMBER
     * @param setAsDefault 是否设置为默认租户（如果用户还没有默认租户，会自动设置为默认）
     * @return 用户租户关联对象
     * @throws IllegalArgumentException 如果关联已存在
     */
    @Transactional
    public UserTenant associateUserWithTenant(Long userId, Long tenantId, 
                                               UserTenant.RelationshipType relationshipType, 
                                               boolean setAsDefault) {
        // 检查关联是否已存在
        Optional<UserTenant> existingRelation = userTenantRepository
                .findByUserIdAndTenantId(userId, tenantId);
        
        if (existingRelation.isPresent()) {
            throw new IllegalArgumentException("用户已关联该租户");
        }

        // 创建新的关联
        UserTenant userTenant = new UserTenant();
        userTenant.setUserId(userId);
        userTenant.setTenantId(tenantId);
        userTenant.setRelationshipType(relationshipType != null ? relationshipType : UserTenant.RelationshipType.MEMBER);

        // 处理默认租户逻辑
        if (setAsDefault) {
            // 如果明确要求设置为默认，先取消当前默认租户
            Optional<UserTenant> currentDefault = userTenantRepository.findByUserIdAndIsDefaultTrue(userId);
            if (currentDefault.isPresent()) {
                currentDefault.get().setIsDefault(false);
                userTenantRepository.save(currentDefault.get());
            }
            userTenant.setIsDefault(true);
        } else {
            // 如果用户还没有默认租户，自动设置为默认
            Optional<UserTenant> defaultTenant = userTenantRepository.findByUserIdAndIsDefaultTrue(userId);
            userTenant.setIsDefault(defaultTenant.isEmpty());
        }

        return userTenantRepository.save(userTenant);
    }

    /**
     * 关联用户和租户（简化版本，使用默认参数）
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 用户租户关联对象
     */
    @Transactional
    public UserTenant associateUserWithTenant(Long userId, Long tenantId) {
        return associateUserWithTenant(userId, tenantId, UserTenant.RelationshipType.MEMBER, false);
    }

    /**
     * 检查用户是否已关联租户
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 如果已关联返回true，否则返回false
     */
    public boolean isUserAssociatedWithTenant(Long userId, Long tenantId) {
        return userTenantRepository.findByUserIdAndTenantId(userId, tenantId).isPresent();
    }

    /**
     * 获取用户和租户的关联关系
     * 
     * @param userId 用户ID
     * @param tenantId 租户ID
     * @return 关联关系，如果不存在返回Optional.empty()
     */
    public Optional<UserTenant> getUserTenantAssociation(Long userId, Long tenantId) {
        return userTenantRepository.findByUserIdAndTenantId(userId, tenantId);
    }
}

