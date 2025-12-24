package com.shaxian.biz.repository;

import com.shaxian.biz.entity.DyeingOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DyeingOrderItemRepository extends JpaRepository<DyeingOrderItem, Long> {
    List<DyeingOrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}

