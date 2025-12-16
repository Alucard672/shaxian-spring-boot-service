package com.shaxian.service.inventory;

import com.shaxian.entity.*;
import com.shaxian.repository.*;
import com.shaxian.util.OrderNumberGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final AdjustmentOrderRepository adjustmentOrderRepository;
    private final AdjustmentOrderItemRepository adjustmentOrderItemRepository;
    private final InventoryCheckOrderRepository inventoryCheckOrderRepository;
    private final InventoryCheckItemRepository inventoryCheckItemRepository;
    private final BatchRepository batchRepository;

    public InventoryService(AdjustmentOrderRepository adjustmentOrderRepository,
                            AdjustmentOrderItemRepository adjustmentOrderItemRepository,
                            InventoryCheckOrderRepository inventoryCheckOrderRepository,
                            InventoryCheckItemRepository inventoryCheckItemRepository,
                            BatchRepository batchRepository) {
        this.adjustmentOrderRepository = adjustmentOrderRepository;
        this.adjustmentOrderItemRepository = adjustmentOrderItemRepository;
        this.inventoryCheckOrderRepository = inventoryCheckOrderRepository;
        this.inventoryCheckItemRepository = inventoryCheckItemRepository;
        this.batchRepository = batchRepository;
    }

    // ===== 库存调整单 =====
    public List<AdjustmentOrder> getAdjustments(String status, String type) {
        List<AdjustmentOrder> orders = adjustmentOrderRepository.findByFilters(status, type);
        orders.forEach(order -> order.setItems(adjustmentOrderItemRepository.findByOrderId(order.getId())));
        return orders;
    }

    public Optional<AdjustmentOrder> getAdjustmentById(Long id) {
        Optional<AdjustmentOrder> order = adjustmentOrderRepository.findById(id);
        order.ifPresent(o -> o.setItems(adjustmentOrderItemRepository.findByOrderId(id)));
        return order;
    }

    @Transactional
    public AdjustmentOrder createAdjustment(AdjustmentOrder order, List<AdjustmentOrderItem> items) {
        order.setOrderNumber(OrderNumberGenerator.generateAdjustmentOrderNumber());

        BigDecimal totalQuantity = items.stream()
                .map(item -> item.getQuantity().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalQuantity(totalQuantity);

        AdjustmentOrder saved = adjustmentOrderRepository.save(order);

        for (AdjustmentOrderItem item : items) {
            item.setOrderId(saved.getId());
        }
        adjustmentOrderItemRepository.saveAll(items);

        if (order.getStatus() == AdjustmentOrder.OrderStatus.COMPLETED) {
            for (AdjustmentOrderItem item : items) {
                batchRepository.increaseStock(item.getBatchId(), item.getQuantity());
            }
        }

        saved.setItems(items);
        return saved;
    }

    @Transactional
    public AdjustmentOrder updateAdjustment(Long id, AdjustmentOrder order, List<AdjustmentOrderItem> items) {
        AdjustmentOrder existing = adjustmentOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("库存调整单不存在"));

        AdjustmentOrder.OrderStatus oldStatus = existing.getStatus();

        if (order.getType() != null) {
            existing.setType(order.getType());
        }
        if (order.getAdjustmentDate() != null) {
            existing.setAdjustmentDate(order.getAdjustmentDate());
        }
        if (order.getOperator() != null) {
            existing.setOperator(order.getOperator());
        }
        if (order.getRemark() != null) {
            existing.setRemark(order.getRemark());
        }
        if (order.getStatus() != null) {
            existing.setStatus(order.getStatus());
        }

        BigDecimal totalQuantity = items.stream()
                .map(item -> item.getQuantity().abs())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        existing.setTotalQuantity(totalQuantity);

        if (oldStatus == AdjustmentOrder.OrderStatus.COMPLETED
                && existing.getStatus() == AdjustmentOrder.OrderStatus.DRAFT) {
            List<AdjustmentOrderItem> oldItems = adjustmentOrderItemRepository.findByOrderId(id);
            for (AdjustmentOrderItem item : oldItems) {
                batchRepository.decreaseStock(item.getBatchId(), item.getQuantity());
            }
        }

        adjustmentOrderItemRepository.deleteByOrderId(id);

        for (AdjustmentOrderItem item : items) {
            item.setOrderId(id);
        }
        adjustmentOrderItemRepository.saveAll(items);

        if (existing.getStatus() == AdjustmentOrder.OrderStatus.COMPLETED) {
            for (AdjustmentOrderItem item : items) {
                batchRepository.increaseStock(item.getBatchId(), item.getQuantity());
            }
        }

        AdjustmentOrder saved = adjustmentOrderRepository.save(existing);
        saved.setItems(items);
        return saved;
    }

    // ===== 盘点单 =====
    public List<InventoryCheckOrder> getChecks(String status, String warehouse) {
        List<InventoryCheckOrder> orders = inventoryCheckOrderRepository.findByFilters(status, warehouse);
        orders.forEach(order -> order.setItems(inventoryCheckItemRepository.findByOrderId(order.getId())));
        return orders;
    }

    public Optional<InventoryCheckOrder> getCheckById(Long id) {
        Optional<InventoryCheckOrder> order = inventoryCheckOrderRepository.findById(id);
        order.ifPresent(o -> o.setItems(inventoryCheckItemRepository.findByOrderId(id)));
        return order;
    }

    @Transactional
    public InventoryCheckOrder createCheck(InventoryCheckOrder order, List<InventoryCheckItem> items) {
        order.setOrderNumber(OrderNumberGenerator.generateInventoryCheckOrderNumber());

        int progressTotal = items.size();
        int progressCompleted = (int) items.stream()
                .filter(item -> item.getActualQuantity() != null)
                .count();

        BigDecimal surplus = BigDecimal.ZERO;
        BigDecimal deficit = BigDecimal.ZERO;
        for (InventoryCheckItem item : items) {
            if (item.getActualQuantity() != null && item.getSystemQuantity() != null) {
                BigDecimal diff = item.getActualQuantity().subtract(item.getSystemQuantity());
                item.setDifference(diff);
                if (diff.compareTo(BigDecimal.ZERO) > 0) {
                    surplus = surplus.add(diff);
                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                    deficit = deficit.add(diff.abs());
                }
            }
        }

        order.setProgressTotal(progressTotal);
        order.setProgressCompleted(progressCompleted);
        order.setSurplus(surplus);
        order.setDeficit(deficit);

        InventoryCheckOrder saved = inventoryCheckOrderRepository.save(order);

        for (InventoryCheckItem item : items) {
            item.setOrderId(saved.getId());
        }
        inventoryCheckItemRepository.saveAll(items);
        saved.setItems(items);

        return saved;
    }

    @Transactional
    public InventoryCheckOrder updateCheck(Long id, InventoryCheckOrder order, List<InventoryCheckItem> items) {
        InventoryCheckOrder existing = inventoryCheckOrderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("盘点单不存在"));

        if (order.getName() != null) {
            existing.setName(order.getName());
        }
        if (order.getWarehouse() != null) {
            existing.setWarehouse(order.getWarehouse());
        }
        if (order.getPlanDate() != null) {
            existing.setPlanDate(order.getPlanDate());
        }
        if (order.getOperator() != null) {
            existing.setOperator(order.getOperator());
        }
        if (order.getRemark() != null) {
            existing.setRemark(order.getRemark());
        }
        if (order.getStatus() != null) {
            existing.setStatus(order.getStatus());
        }

        int progressTotal = items.size();
        int progressCompleted = (int) items.stream()
                .filter(item -> item.getActualQuantity() != null)
                .count();

        BigDecimal surplus = BigDecimal.ZERO;
        BigDecimal deficit = BigDecimal.ZERO;
        for (InventoryCheckItem item : items) {
            if (item.getActualQuantity() != null && item.getSystemQuantity() != null) {
                BigDecimal diff = item.getActualQuantity().subtract(item.getSystemQuantity());
                item.setDifference(diff);
                if (diff.compareTo(BigDecimal.ZERO) > 0) {
                    surplus = surplus.add(diff);
                } else if (diff.compareTo(BigDecimal.ZERO) < 0) {
                    deficit = deficit.add(diff.abs());
                }
            }
        }

        existing.setProgressTotal(progressTotal);
        existing.setProgressCompleted(progressCompleted);
        existing.setSurplus(surplus);
        existing.setDeficit(deficit);

        inventoryCheckItemRepository.deleteByOrderId(id);

        for (InventoryCheckItem item : items) {
            item.setOrderId(id);
        }
        inventoryCheckItemRepository.saveAll(items);

        InventoryCheckOrder saved = inventoryCheckOrderRepository.save(existing);
        saved.setItems(items);
        return saved;
    }

    @Transactional
    public void deleteCheck(Long id) {
        if (!inventoryCheckOrderRepository.existsById(id)) {
            throw new IllegalArgumentException("盘点单不存在");
        }
        inventoryCheckOrderRepository.deleteById(id);
    }
}
