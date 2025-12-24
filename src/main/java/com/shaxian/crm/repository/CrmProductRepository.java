package com.shaxian.crm.repository;

import com.shaxian.crm.entity.CrmProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrmProductRepository extends JpaRepository<CrmProduct, Long>, JpaSpecificationExecutor<CrmProduct> {
    Optional<CrmProduct> findByCode(String code);
    boolean existsByCode(String code);
}

