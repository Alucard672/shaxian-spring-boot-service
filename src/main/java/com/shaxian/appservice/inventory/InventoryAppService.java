package com.shaxian.appservice.inventory;

import com.shaxian.entity.AdjustmentOrder;
import com.shaxian.entity.AdjustmentOrderItem;
import com.shaxian.entity.InventoryCheckItem;
import com.shaxian.entity.InventoryCheckOrder;
import com.shaxian.service.inventory.InventoryService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryAppService {

    private final InventoryService inventoryService;

    public InventoryAppService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ===== 库存调整单 =====
    public List<AdjustmentOrder> listAdjustments(String status, String type) {
        return inventoryService.getAdjustments(status, type);
    }

    public Optional<AdjustmentOrder> findAdjustment(Long id) {
        return inventoryService.getAdjustmentById(id);
    }

    public AdjustmentOrder createAdjustment(Map<String, Object> request) {
        AdjustmentOrder order = buildAdjustmentOrderFromRequest(request);
        List<AdjustmentOrderItem> items = buildAdjustmentItemsFromRequest(request);
        return inventoryService.createAdjustment(order, items);
    }

    public AdjustmentOrder updateAdjustment(Long id, Map<String, Object> request) {
        AdjustmentOrder order = buildAdjustmentOrderFromRequest(request);
        List<AdjustmentOrderItem> items = buildAdjustmentItemsFromRequest(request);
        return inventoryService.updateAdjustment(id, order, items);
    }

    // ===== 盘点单 =====
    public List<InventoryCheckOrder> listChecks(String status, String warehouse) {
        return inventoryService.getChecks(status, warehouse);
    }

    public Optional<InventoryCheckOrder> findCheck(Long id) {
        return inventoryService.getCheckById(id);
    }

    public InventoryCheckOrder createCheck(Map<String, Object> request) {
        InventoryCheckOrder order = buildCheckOrderFromRequest(request);
        List<InventoryCheckItem> items = buildCheckItemsFromRequest(request);
        return inventoryService.createCheck(order, items);
    }

    public InventoryCheckOrder updateCheck(Long id, Map<String, Object> request) {
        InventoryCheckOrder order = buildCheckOrderFromRequest(request);
        List<InventoryCheckItem> items = buildCheckItemsFromRequest(request);
        return inventoryService.updateCheck(id, order, items);
    }

    public void deleteCheck(Long id) {
        inventoryService.deleteCheck(id);
    }

    // ===== 构建对象 =====
    private AdjustmentOrder buildAdjustmentOrderFromRequest(Map<String, Object> request) {
        AdjustmentOrder order = new AdjustmentOrder();
        if (request.containsKey("type")) {
            order.setType(AdjustmentOrder.AdjustmentType.valueOf((String) request.get("type")));
        }
        if (request.containsKey("adjustmentDate")) {
            order.setAdjustmentDate(LocalDate.parse((String) request.get("adjustmentDate")));
        }
        if (request.containsKey("operator")) {
            order.setOperator((String) request.get("operator"));
        }
        if (request.containsKey("remark")) {
            order.setRemark((String) request.get("remark"));
        }
        if (request.containsKey("status")) {
            order.setStatus(AdjustmentOrder.OrderStatus.valueOf((String) request.get("status")));
        }
        return order;
    }

    @SuppressWarnings("unchecked")
    private List<AdjustmentOrderItem> buildAdjustmentItemsFromRequest(Map<String, Object> request) {
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
        if (itemsData == null) {
            throw new IllegalArgumentException("items 不能为空");
        }
        return itemsData.stream().map(itemData -> {
            AdjustmentOrderItem item = new AdjustmentOrderItem();
            if (itemData.containsKey("batchId")) {
                item.setBatchId(parseLong(itemData.get("batchId")));
            }
            if (itemData.containsKey("batchCode")) {
                item.setBatchCode((String) itemData.get("batchCode"));
            }
            if (itemData.containsKey("productId")) {
                item.setProductId(parseLong(itemData.get("productId")));
            }
            if (itemData.containsKey("productName")) {
                item.setProductName((String) itemData.get("productName"));
            }
            if (itemData.containsKey("colorId")) {
                item.setColorId(parseLong(itemData.get("colorId")));
            }
            if (itemData.containsKey("colorName")) {
                item.setColorName((String) itemData.get("colorName"));
            }
            if (itemData.containsKey("colorCode")) {
                item.setColorCode((String) itemData.get("colorCode"));
            }
            if (itemData.containsKey("quantity")) {
                item.setQuantity(new BigDecimal(itemData.get("quantity").toString()));
            }
            if (itemData.containsKey("unit")) {
                item.setUnit((String) itemData.get("unit"));
            }
            if (itemData.containsKey("remark")) {
                item.setRemark((String) itemData.get("remark"));
            }
            return item;
        }).toList();
    }

    private InventoryCheckOrder buildCheckOrderFromRequest(Map<String, Object> request) {
        InventoryCheckOrder order = new InventoryCheckOrder();
        if (request.containsKey("name")) {
            order.setName((String) request.get("name"));
        }
        if (request.containsKey("warehouse")) {
            order.setWarehouse((String) request.get("warehouse"));
        }
        if (request.containsKey("planDate")) {
            order.setPlanDate(LocalDate.parse((String) request.get("planDate")));
        }
        if (request.containsKey("operator")) {
            order.setOperator((String) request.get("operator"));
        }
        if (request.containsKey("remark")) {
            order.setRemark((String) request.get("remark"));
        }
        if (request.containsKey("status")) {
            order.setStatus(InventoryCheckOrder.OrderStatus.valueOf((String) request.get("status")));
        }
        return order;
    }

    @SuppressWarnings("unchecked")
    private List<InventoryCheckItem> buildCheckItemsFromRequest(Map<String, Object> request) {
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
        if (itemsData == null) {
            throw new IllegalArgumentException("items 不能为空");
        }
        return itemsData.stream().map(itemData -> {
            InventoryCheckItem item = new InventoryCheckItem();
            if (itemData.containsKey("batchId")) {
                item.setBatchId(parseLong(itemData.get("batchId")));
            }
            if (itemData.containsKey("batchCode")) {
                item.setBatchCode((String) itemData.get("batchCode"));
            }
            if (itemData.containsKey("productId")) {
                item.setProductId(parseLong(itemData.get("productId")));
            }
            if (itemData.containsKey("productName")) {
                item.setProductName((String) itemData.get("productName"));
            }
            if (itemData.containsKey("colorId")) {
                item.setColorId(parseLong(itemData.get("colorId")));
            }
            if (itemData.containsKey("colorName")) {
                item.setColorName((String) itemData.get("colorName"));
            }
            if (itemData.containsKey("colorCode")) {
                item.setColorCode((String) itemData.get("colorCode"));
            }
            if (itemData.containsKey("systemQuantity")) {
                item.setSystemQuantity(new BigDecimal(itemData.get("systemQuantity").toString()));
            }
            if (itemData.containsKey("actualQuantity") && itemData.get("actualQuantity") != null) {
                item.setActualQuantity(new BigDecimal(itemData.get("actualQuantity").toString()));
            }
            if (itemData.containsKey("unit")) {
                item.setUnit((String) itemData.get("unit"));
            }
            if (itemData.containsKey("remark")) {
                item.setRemark((String) itemData.get("remark"));
            }
            return item;
        }).toList();
    }

    private Long parseLong(Object value) {
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Integer) return ((Integer) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
