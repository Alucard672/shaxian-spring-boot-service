package com.shaxian.repository;

import com.shaxian.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {
    List<PurchaseOrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
    List<PurchaseOrderItem> findAllByTenantId(Long tenantId);
    Optional<PurchaseOrderItem> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByIdAndTenantId(Long id, Long tenantId);
}

