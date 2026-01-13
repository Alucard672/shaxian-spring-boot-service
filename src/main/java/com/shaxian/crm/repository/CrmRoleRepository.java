package com.shaxian.crm.repository;

import com.shaxian.crm.entity.CrmRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CrmRoleRepository extends JpaRepository<CrmRole, Long>, JpaSpecificationExecutor<CrmRole> {
    /**
     * 根据角色代码查找角色
     */
    Optional<CrmRole> findByCode(String code);

    /**
     * 检查角色代码是否存在
     */
    boolean existsByCode(String code);

    /**
     * 检查角色代码是否存在（排除指定ID）
     */
    boolean existsByCodeAndIdNot(String code, Long id);

    /**
     * 根据ID列表批量查找角色
     */
    List<CrmRole> findByIdIn(List<Long> ids);

    /**
     * 根据ID列表和状态批量查找角色
     */
    List<CrmRole> findByIdInAndStatus(List<Long> ids, CrmRole.RoleStatus status);
}
