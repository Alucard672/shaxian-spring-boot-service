package com.shaxian.repository;

import com.shaxian.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {
    @Query("SELECT so FROM SalesOrder so WHERE " +
           "(:status IS NULL OR CAST(so.status AS string) = :status) AND " +
           "(:customerId IS NULL OR so.customerId = :customerId) AND " +
           "(:startDate IS NULL OR so.salesDate >= :startDate) AND " +
           "(:endDate IS NULL OR so.salesDate <= :endDate) " +
           "ORDER BY so.createdAt DESC")
    List<SalesOrder> findByFilters(@Param("status") String status,
                                   @Param("customerId") Long customerId,
                                   @Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate);
    
    List<SalesOrder> findAllByTenantId(Long tenantId);
    Optional<SalesOrder> findByIdAndTenantId(Long id, Long tenantId);
    boolean existsByIdAndTenantId(Long id, Long tenantId);
}

