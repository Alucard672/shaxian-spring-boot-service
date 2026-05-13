package com.shaxian.biz.repository;

import com.shaxian.biz.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long>, JpaSpecificationExecutor<Tenant> {
    Optional<Tenant> findByCode(String code);
    boolean existsByCode(String code);

    long countByStatus(Tenant.TenantStatus status);

    List<Tenant> findByExpiresAtBetween(LocalDateTime start, LocalDateTime end);

    List<Tenant> findByExpiresAtBefore(LocalDateTime time);
}
