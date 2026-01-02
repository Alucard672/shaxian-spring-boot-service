package com.shaxian.crm.repository;

import com.shaxian.crm.entity.CrmSalesOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CrmSalesOrderRepository extends JpaRepository<CrmSalesOrder, Long>, JpaSpecificationExecutor<CrmSalesOrder> {
    Optional<CrmSalesOrder> findByOrderNumber(String orderNumber);
    
    @Query("SELECT o FROM CrmSalesOrder o WHERE " +
           "(:crmCustomerId IS NULL OR o.crmCustomerId = :crmCustomerId) AND " +
           "(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.salesDate >= :startDate) AND " +
           "(:endDate IS NULL OR o.salesDate <= :endDate)")
    List<CrmSalesOrder> findByFilters(
            @Param("crmCustomerId") Long crmCustomerId,
            @Param("status") CrmSalesOrder.OrderStatus status,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
    
    Page<CrmSalesOrder> findByCrmCustomerId(Long crmCustomerId, Pageable pageable);
}

