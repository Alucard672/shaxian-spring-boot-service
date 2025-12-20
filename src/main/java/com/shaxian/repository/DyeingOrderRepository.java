package com.shaxian.repository;

import com.shaxian.entity.DyeingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DyeingOrderRepository extends JpaRepository<DyeingOrder, Long> {
    @Query("SELECT do FROM DyeingOrder do WHERE " +
           "(:status IS NULL OR CAST(do.status AS string) = :status) AND " +
           "(:productId IS NULL OR do.productId = :productId) " +
           "ORDER BY do.createdAt DESC")
    List<DyeingOrder> findByFilters(@Param("status") String status, @Param("productId") Long productId);
    
    List<DyeingOrder> findAllByTenantId(Long tenantId);
    Optional<DyeingOrder> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByIdAndTenantId(Long id, Long tenantId);
}

