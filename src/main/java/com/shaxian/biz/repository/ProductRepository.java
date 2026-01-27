package com.shaxian.biz.repository;

import com.shaxian.biz.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    boolean existsByCode(String code);
    
    /**
     * 根据ID和租户ID查询商品
     * 用于分享码场景，需要显式指定tenantId
     * 使用JPQL查询，配合TenantContext设置正确的tenantId
     */
    @Query("SELECT p FROM Product p WHERE p.id = :id AND p.tenantId = :tenantId")
    Optional<Product> findByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
    
    /**
     * 根据ID和租户ID检查商品是否存在
     * 用于分享码场景，需要显式指定tenantId
     * 使用JPQL查询，配合TenantContext设置正确的tenantId
     */
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.id = :id AND p.tenantId = :tenantId")
    boolean existsByIdAndTenantId(@Param("id") Long id, @Param("tenantId") Long tenantId);
}

