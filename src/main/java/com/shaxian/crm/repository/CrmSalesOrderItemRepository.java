package com.shaxian.crm.repository;

import com.shaxian.crm.entity.CrmSalesOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrmSalesOrderItemRepository extends JpaRepository<CrmSalesOrderItem, Long> {
    List<CrmSalesOrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}

