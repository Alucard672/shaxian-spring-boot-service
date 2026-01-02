package com.shaxian.biz.repository;

import com.shaxian.biz.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long>, JpaSpecificationExecutor<Tenant> {
    Optional<Tenant> findByCode(String code);
    boolean existsByCode(String code);
    Optional<Tenant> findByCrmCustomerId(Long crmCustomerId);
}
