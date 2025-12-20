package com.shaxian.repository;

import com.shaxian.entity.DyeingOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DyeingOrderItemRepository extends JpaRepository<DyeingOrderItem, Long> {
    List<DyeingOrderItem> findByOrderId(Long orderId);
    void deleteByOrderId(Long orderId);
}

