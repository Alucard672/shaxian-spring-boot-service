package com.shaxian.biz.repository;

import com.shaxian.biz.entity.AdjustmentOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdjustmentOrderItemRepository extends JpaRepository<AdjustmentOrderItem, Long> {
    List<AdjustmentOrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}

