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
}
