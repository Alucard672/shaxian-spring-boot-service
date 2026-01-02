package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmSalesOrder;
import com.shaxian.crm.entity.CrmSalesOrderItem;
import com.shaxian.crm.repository.CrmSalesOrderRepository;
import com.shaxian.crm.repository.CrmSalesOrderItemRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CrmSalesService {
    
    private final CrmSalesOrderRepository salesOrderRepository;
    private final CrmSalesOrderItemRepository salesOrderItemRepository;

    public CrmSalesService(
            CrmSalesOrderRepository salesOrderRepository,
            CrmSalesOrderItemRepository salesOrderItemRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderItemRepository = salesOrderItemRepository;
    }

    public List<CrmSalesOrder> getAllSales(Long crmCustomerId, String status, LocalDate startDate, LocalDate endDate) {
        CrmSalesOrder.OrderStatus statusEnum = null;
        if (status != null && !status.isEmpty()) {
            try {
                statusEnum = CrmSalesOrder.OrderStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                // 忽略无效的状态值
            }
        }
        return salesOrderRepository.findByFilters(crmCustomerId, statusEnum, startDate, endDate);
    }

    public Page<CrmSalesOrder> querySales(Long crmCustomerId, String status, LocalDate startDate, LocalDate endDate, Integer pageNo, Integer pageSize) {
        Specification<CrmSalesOrder> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (crmCustomerId != null) {
                predicates.add(cb.equal(root.get("crmCustomerId"), crmCustomerId));
            }
            if (status != null && !status.isEmpty()) {
                try {
                    CrmSalesOrder.OrderStatus statusEnum = CrmSalesOrder.OrderStatus.valueOf(status);
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的状态值
                }
            }
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("salesDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("salesDate"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return salesOrderRepository.findAll(spec, pageable);
    }

    public Optional<CrmSalesOrder> getSalesById(Long id) {
        Optional<CrmSalesOrder> order = salesOrderRepository.findById(id);
        order.ifPresent(o -> o.setItems(salesOrderItemRepository.findByOrderId(id)));
        return order;
    }

    @Transactional
    public CrmSalesOrder createSales(CrmSalesOrder order, List<CrmSalesOrderItem> items) {
        // 生成订单号
        String orderNumber = generateCrmSalesOrderNumber();
        order.setOrderNumber(orderNumber);
        
        // 计算总金额
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(paidAmount);
        order.setUnpaidAmount(totalAmount.subtract(paidAmount));
        
        // 保存订单
        CrmSalesOrder saved = salesOrderRepository.save(order);
        
        // 保存订单项
        for (CrmSalesOrderItem item : items) {
            item.setOrderId(saved.getId());
            item.setAmount(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        salesOrderItemRepository.saveAll(items);
        
        saved.setItems(items);
        return saved;
    }

    @Transactional
    public CrmSalesOrder updateSales(Long id, CrmSalesOrder order, List<CrmSalesOrderItem> items) {
        CrmSalesOrder existing = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售单不存在"));
        
        // 如果订单不是草稿状态，只允许更新状态、已付金额等字段，不允许修改订单项
        if (existing.getStatus() != CrmSalesOrder.OrderStatus.DRAFT) {
            // 只更新允许的字段
            if (order.getStatus() != null) {
                existing.setStatus(order.getStatus());
            }
            if (order.getPaidAmount() != null) {
                existing.setPaidAmount(order.getPaidAmount());
                existing.setUnpaidAmount(existing.getTotalAmount().subtract(order.getPaidAmount()));
            }
            if (order.getOperator() != null) {
                existing.setOperator(order.getOperator());
            }
            if (order.getRemark() != null) {
                existing.setRemark(order.getRemark());
            }
            CrmSalesOrder saved = salesOrderRepository.save(existing);
            saved.setItems(salesOrderItemRepository.findByOrderId(id));
            return saved;
        }
        
        // 草稿状态的订单可以完整修改
        // 计算总金额
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
        order.setId(id);
        order.setOrderNumber(existing.getOrderNumber());
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(paidAmount);
        order.setUnpaidAmount(totalAmount.subtract(paidAmount));
        order.setCreatedAt(existing.getCreatedAt());
        
        // 删除旧订单项
        salesOrderItemRepository.deleteByOrderId(id);
        
        // 保存新订单项
        for (CrmSalesOrderItem item : items) {
            item.setOrderId(id);
            item.setAmount(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        salesOrderItemRepository.saveAll(items);
        
        CrmSalesOrder saved = salesOrderRepository.save(order);
        saved.setItems(items);
        return saved;
    }

    @Transactional
    public void deleteSales(Long id) {
        CrmSalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售单不存在"));
        
        if (order.getStatus() != CrmSalesOrder.OrderStatus.DRAFT) {
            throw new IllegalArgumentException("只能删除草稿状态的订单");
        }
        
        salesOrderRepository.deleteById(id);
    }

    /**
     * 生成软件销售订单号
     * 格式：RJS + yyyyMMdd + 3位随机数
     */
    private String generateCrmSalesOrderNumber() {
        java.time.LocalDate date = java.time.LocalDate.now();
        String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequence = String.format("%03d", new java.util.Random().nextInt(1000));
        return "RJS" + dateStr + sequence;
    }
}

