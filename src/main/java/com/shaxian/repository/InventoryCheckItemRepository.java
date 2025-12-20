package com.shaxian.repository;

import com.shaxian.entity.InventoryCheckItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryCheckItemRepository extends JpaRepository<InventoryCheckItem, Long> {
    List<InventoryCheckItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
    List<InventoryCheckItem> findAllByTenantId(Long tenantId);
    Optional<InventoryCheckItem> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByIdAndTenantId(Long id, Long tenantId);
}

