package com.shaxian.repository;

import com.shaxian.entity.AdjustmentOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdjustmentOrderItemRepository extends JpaRepository<AdjustmentOrderItem, Long> {
    List<AdjustmentOrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}

