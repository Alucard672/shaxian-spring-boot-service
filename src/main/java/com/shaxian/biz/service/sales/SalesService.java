package com.shaxian.biz.service.sales;

import com.shaxian.biz.entity.AccountReceivable;
import com.shaxian.biz.entity.Batch;
import com.shaxian.biz.entity.SalesOrder;
import com.shaxian.biz.entity.SalesOrderItem;
import com.shaxian.biz.repository.AccountReceivableRepository;
import com.shaxian.biz.repository.BatchRepository;
import com.shaxian.biz.repository.SalesOrderRepository;
import com.shaxian.biz.repository.SalesOrderItemRepository;
import com.shaxian.biz.service.settings.SystemParamsService;
import com.shaxian.biz.util.OrderNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {
    private final SalesOrderRepository salesOrderRepository;
    private final SalesOrderItemRepository salesOrderItemRepository;
    private final BatchRepository batchRepository;
    private final SystemParamsService systemParamsService;
    private final AccountReceivableRepository accountReceivableRepository;

    public SalesService(
            SalesOrderRepository salesOrderRepository,
            SalesOrderItemRepository salesOrderItemRepository,
            BatchRepository batchRepository,
            SystemParamsService systemParamsService,
            AccountReceivableRepository accountReceivableRepository) {
        this.salesOrderRepository = salesOrderRepository;
        this.salesOrderItemRepository = salesOrderItemRepository;
        this.batchRepository = batchRepository;
        this.systemParamsService = systemParamsService;
        this.accountReceivableRepository = accountReceivableRepository;
    }


    public List<SalesOrder> getAllSales(String status, Long customerId, LocalDate startDate, LocalDate endDate) {
        return salesOrderRepository.findByFilters(status, customerId, startDate, endDate);
    }

    public Optional<SalesOrder> getSalesById(Long id) {
        Optional<SalesOrder> order = salesOrderRepository.findById(id);
        order.ifPresent(o -> o.setItems(salesOrderItemRepository.findByOrderId(id)));
        return order;
    }

    @Transactional
    public SalesOrder createSales(SalesOrder order, List<SalesOrderItem> items) {
        order.setOrderNumber(OrderNumberGenerator.generateSalesOrderNumber());
        
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getQuantity().multiply(item.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal receivedAmount = order.getReceivedAmount() != null ? order.getReceivedAmount() : BigDecimal.ZERO;
        order.setTotalAmount(totalAmount);
        order.setReceivedAmount(receivedAmount);
        order.setUnpaidAmount(totalAmount.subtract(receivedAmount));
        
        SalesOrder saved = salesOrderRepository.save(order);
        
        for (SalesOrderItem item : items) {
            item.setOrderId(saved.getId());
            item.setAmount(item.getQuantity().multiply(item.getPrice()));
        }
        salesOrderItemRepository.saveAll(items);
        
        // 如果状态是已出库，按系统参数决定是否校验缸号库存，再扣减（batch_id 为 null 表示无缸号不扣减）
        if (order.getStatus() == SalesOrder.OrderStatus.SHIPPED) {
            boolean allowNegativeStock = Boolean.TRUE.equals(systemParamsService.getSystemParams().getAllowNegativeStock());
            if (!allowNegativeStock) {
                for (SalesOrderItem item : items) {
                    if (item.getBatchId() != null) {
                        Batch batch = batchRepository.findById(item.getBatchId())
                                .orElseThrow(() -> new IllegalArgumentException("缸号不存在: " + item.getBatchId()));
                        if (batch.getStockQuantity() == null || item.getQuantity().compareTo(batch.getStockQuantity()) > 0) {
                            throw new IllegalArgumentException("缸号库存不足: " + batch.getCode() + "，当前库存 " + batch.getStockQuantity() + "，出库数量 " + item.getQuantity());
                        }
                    }
                }
            }
            for (SalesOrderItem item : items) {
                if (item.getBatchId() != null) {
                    batchRepository.decreaseStock(item.getBatchId(), item.getQuantity());
                }
            }
        }
        
        saved.setItems(items);
        return saved;
    }

    @Transactional
    public SalesOrder updateSales(Long id, SalesOrder order, List<SalesOrderItem> items) {
        SalesOrder existing = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售单不存在或无权访问"));
        
        if (existing.getStatus() != SalesOrder.OrderStatus.DRAFT) {
            throw new IllegalArgumentException("只能修改草稿状态的订单");
        }
        
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getQuantity().multiply(item.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal receivedAmount = order.getReceivedAmount() != null ? order.getReceivedAmount() : BigDecimal.ZERO;
        order.setId(id);
        order.setOrderNumber(existing.getOrderNumber());
        order.setTotalAmount(totalAmount);
        order.setReceivedAmount(receivedAmount);
        order.setUnpaidAmount(totalAmount.subtract(receivedAmount));
        order.setCreatedAt(existing.getCreatedAt());
        
        salesOrderItemRepository.deleteByOrderId(id);
        
        for (SalesOrderItem item : items) {
            item.setOrderId(id);
            item.setAmount(item.getQuantity().multiply(item.getPrice()));
        }
        salesOrderItemRepository.saveAll(items);
        
        SalesOrder saved = salesOrderRepository.save(order);
        saved.setItems(items);
        return saved;
    }

    @Transactional
    public void deleteSales(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售单不存在或无权访问"));

        if (order.getStatus() != SalesOrder.OrderStatus.DRAFT) {
            throw new IllegalArgumentException("只能删除草稿状态的订单");
        }

        salesOrderRepository.deleteById(id);
    }

    /**
     * 作废销售单（事务）：
     *  - 校验：草稿可作废（直接 delete 也可，这里统一软删）；已审核 / 已出库 / 已完成都可；已作废拒绝重复操作
     *  - 若已收过款（receivedAmount > 0），拒绝作废；要求先做收款冲销
     *  - 若已出库（SHIPPED）/ APPROVED 等占用库存的状态，还库存（按 batch_id 增加 stock）
     *  - 同步把关联应收账款标记为 VOIDED（前提是该应收 receivedAmount = 0）
     *  - 销售单状态置为 CANCELLED
     */
    @Transactional
    public SalesOrder cancelSales(Long id) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售单不存在或无权访问"));

        if (order.getStatus() == SalesOrder.OrderStatus.CANCELLED) {
            throw new IllegalArgumentException("该订单已作废，无需重复操作");
        }

        BigDecimal received = order.getReceivedAmount() != null ? order.getReceivedAmount() : BigDecimal.ZERO;
        if (received.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalArgumentException(
                    "该订单已收款 " + received + " 元，请先冲销收款后再作废订单");
        }

        // 还库存：之前 SHIPPED 时减过的，加回去
        if (order.getStatus() == SalesOrder.OrderStatus.SHIPPED) {
            List<SalesOrderItem> items = salesOrderItemRepository.findByOrderId(id);
            for (SalesOrderItem item : items) {
                if (item.getBatchId() != null && item.getQuantity() != null
                        && item.getQuantity().compareTo(BigDecimal.ZERO) > 0) {
                    batchRepository.increaseStock(item.getBatchId(), item.getQuantity());
                }
            }
        }

        // 冲销关联应收账款（只处理 UNPAID 的；PAID 的不可能走到这里因为前面已拒绝；防御性再校验一次）
        List<AccountReceivable> linked = accountReceivableRepository.findBySalesOrderId(id);
        for (AccountReceivable ar : linked) {
            if (ar.getReceivedAmount() != null
                    && ar.getReceivedAmount().compareTo(BigDecimal.ZERO) > 0) {
                throw new IllegalArgumentException(
                        "应收账款 #" + ar.getId() + " 已收过款，请先处理后再作废订单");
            }
            ar.setStatus(AccountReceivable.AccountStatus.VOIDED);
            ar.setUpdatedAt(LocalDateTime.now());
        }
        if (!linked.isEmpty()) {
            accountReceivableRepository.saveAll(linked);
        }

        order.setStatus(SalesOrder.OrderStatus.CANCELLED);
        return salesOrderRepository.save(order);
    }
}

