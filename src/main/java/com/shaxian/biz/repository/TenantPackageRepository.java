package com.shaxian.biz.repository;

import com.shaxian.biz.entity.TenantPackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantPackageRepository extends JpaRepository<TenantPackage, Long> {
    Optional<TenantPackage> findByName(String name);
}
