package com.shaxian.biz.service.purchase;

import com.shaxian.biz.entity.Batch;
import com.shaxian.biz.entity.PurchaseOrder;
import com.shaxian.biz.entity.PurchaseOrderItem;
import com.shaxian.biz.repository.BatchRepository;
import com.shaxian.biz.repository.PurchaseOrderRepository;
import com.shaxian.biz.repository.PurchaseOrderItemRepository;
import com.shaxian.biz.util.OrderNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PurchaseService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderItemRepository purchaseOrderItemRepository;
    private final BatchRepository batchRepository;

    public PurchaseService(
            PurchaseOrderRepository purchaseOrderRepository,
            PurchaseOrderItemRepository purchaseOrderItemRepository,
            BatchRepository batchRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderItemRepository = purchaseOrderItemRepository;
        this.batchRepository = batchRepository;
    }


    public List<PurchaseOrder> getAllPurchases(String status, Long supplierId, LocalDate startDate, LocalDate endDate) {
        return purchaseOrderRepository.findByFilters(status, supplierId, startDate, endDate);
    }

    public Optional<PurchaseOrder> getPurchaseById(Long id) {
        Optional<PurchaseOrder> order = purchaseOrderRepository.findById(id);
        order.ifPresent(o -> o.setItems(purchaseOrderItemRepository.findByOrderId(id)));
        return order;
    }

    @Transactional
    public PurchaseOrder createPurchase(PurchaseOrder order, List<PurchaseOrderItem> items) {
        order.setOrderNumber(OrderNumberGenerator.generatePurchaseOrderNumber());
        
        // 计算总金额
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getQuantity().multiply(item.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(paidAmount);
        order.setUnpaidAmount(totalAmount.subtract(paidAmount));
        
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        
        // 保存明细
        for (PurchaseOrderItem item : items) {
            item.setOrderId(saved.getId());
            item.setAmount(item.getQuantity().multiply(item.getPrice()));
        }
        purchaseOrderItemRepository.saveAll(items);
        
        if (order.getStatus() == PurchaseOrder.OrderStatus.RECEIVED) {
            applyStockInForReceived(saved, items);
        }
        saved.setItems(items);
        return saved;
    }

    @Transactional
    public PurchaseOrder updatePurchase(Long id, PurchaseOrder order, List<PurchaseOrderItem> items) {
        PurchaseOrder existing = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("进货单不存在或无权访问"));
        
        if (existing.getStatus() != PurchaseOrder.OrderStatus.DRAFT) {
            throw new IllegalArgumentException("只能修改草稿状态的订单");
        }
        
        // 计算总金额
        BigDecimal totalAmount = items.stream()
                .map(item -> item.getQuantity().multiply(item.getPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal paidAmount = order.getPaidAmount() != null ? order.getPaidAmount() : BigDecimal.ZERO;
        order.setId(id);
        order.setOrderNumber(existing.getOrderNumber());
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(paidAmount);
        order.setUnpaidAmount(totalAmount.subtract(paidAmount));
        order.setCreatedAt(existing.getCreatedAt());
        
        // 删除旧明细
        purchaseOrderItemRepository.deleteByOrderId(id);
        
        // 保存新明细
        for (PurchaseOrderItem item : items) {
            item.setOrderId(id);
            item.setAmount(item.getQuantity().multiply(item.getPrice()));
        }
        purchaseOrderItemRepository.saveAll(items);
        
        PurchaseOrder saved = purchaseOrderRepository.save(order);
        if (existing.getStatus() != PurchaseOrder.OrderStatus.RECEIVED && order.getStatus() == PurchaseOrder.OrderStatus.RECEIVED) {
            applyStockInForReceived(saved, items);
        }
        saved.setItems(items);
        return saved;
    }

    /**
     * 进货单状态为 RECEIVED 时，按明细增加对应缸号库存：存在则 increaseStock，不存在则创建缸号后写入库存。
     */
    private void applyStockInForReceived(PurchaseOrder order, List<PurchaseOrderItem> items) {
        Long tenantId = order.getTenantId();
        for (PurchaseOrderItem item : items) {
            if (item.getColorId() == null) {
                throw new IllegalArgumentException("入库时明细必须指定色号(color_id)，缸号: " + item.getBatchCode());
            }
            String batchCode = item.getBatchCode();
            if (batchCode == null || batchCode.isBlank()) {
                throw new IllegalArgumentException("入库时明细必须指定缸号(batch_code)");
            }
            Optional<Batch> existingBatch = batchRepository.findByTenantIdAndCode(tenantId, batchCode);
            if (existingBatch.isPresent()) {
                batchRepository.increaseStock(existingBatch.get().getId(), item.getQuantity());
            } else {
                Batch batch = new Batch();
                batch.setTenantId(tenantId);
                batch.setColorId(item.getColorId());
                batch.setCode(batchCode);
                batch.setInitialQuantity(item.getQuantity());
                batch.setStockQuantity(item.getQuantity());
                batch.setSupplierId(order.getSupplierId());
                batch.setSupplierName(order.getSupplierName());
                batch.setPurchasePrice(item.getPrice());
                batch.setProductionDate(item.getProductionDate());
                batch.setStockLocation(item.getStockLocation());
                batch.setCreatedAt(LocalDateTime.now());
                batch.setUpdatedAt(LocalDateTime.now());
                batchRepository.save(batch);
            }
        }
    }

    @Transactional
    public void deletePurchase(Long id) {
        PurchaseOrder order = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("进货单不存在或无权访问"));
        
        if (order.getStatus() != PurchaseOrder.OrderStatus.DRAFT) {
            throw new IllegalArgumentException("只能删除草稿状态的订单");
        }
        
        purchaseOrderRepository.deleteById(id);
    }
}

