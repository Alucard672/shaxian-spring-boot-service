package com.shaxian.repository;

import com.shaxian.entity.UserTenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTenantRepository extends JpaRepository<UserTenant, Long> {
    List<UserTenant> findByUserId(Long userId);
    Optional<UserTenant> findByUserIdAndTenantId(Long userId, Long tenantId);
    Optional<UserTenant> findByUserIdAndIsDefaultTrue(Long userId);
    List<UserTenant> findByTenantId(Long tenantId);
    
    // 查找用户自己创建的租户（OWNER关系类型），按创建时间降序
    List<UserTenant> findByUserIdAndRelationshipTypeOrderByCreatedAtDesc(Long userId, UserTenant.RelationshipType relationshipType);
    
    // 查找用户的所有租户，按创建时间降序（最新绑定的在前）
    List<UserTenant> findByUserIdOrderByCreatedAtDesc(Long userId);
}
