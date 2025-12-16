package com.shaxian.appservice.purchase;

import com.shaxian.entity.PurchaseOrder;
import com.shaxian.entity.PurchaseOrderItem;
import com.shaxian.service.PurchaseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class PurchaseAppService {

    private final PurchaseService purchaseService;

    public PurchaseAppService(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    public List<PurchaseOrder> listPurchases(String status, Long supplierId, LocalDate startDate, LocalDate endDate) {
        List<PurchaseOrder> orders = purchaseService.getAllPurchases(status, supplierId, startDate, endDate);
        orders.forEach(order -> purchaseService.getPurchaseById(order.getId())
                .ifPresent(full -> order.setItems(full.getItems())));
        return orders;
    }

    public java.util.Optional<PurchaseOrder> findById(Long id) {
        return purchaseService.getPurchaseById(id);
    }

    public PurchaseOrder createPurchase(Map<String, Object> request) {
        PurchaseOrder order = buildOrderFromRequest(request);
        List<PurchaseOrderItem> items = buildItemsFromRequest(request);
        return purchaseService.createPurchase(order, items);
    }

    public PurchaseOrder updatePurchase(Long id, Map<String, Object> request) {
        PurchaseOrder order = buildOrderFromRequest(request);
        List<PurchaseOrderItem> items = buildItemsFromRequest(request);
        return purchaseService.updatePurchase(id, order, items);
    }

    public void deletePurchase(Long id) {
        purchaseService.deletePurchase(id);
    }

    private PurchaseOrder buildOrderFromRequest(Map<String, Object> request) {
        PurchaseOrder order = new PurchaseOrder();
        if (request.containsKey("supplierId")) order.setSupplierId(parseLong(request.get("supplierId")));
        if (request.containsKey("supplierName")) order.setSupplierName((String) request.get("supplierName"));
        if (request.containsKey("purchaseDate")) order.setPurchaseDate(LocalDate.parse((String) request.get("purchaseDate")));
        if (request.containsKey("expectedDate")) order.setExpectedDate(LocalDate.parse((String) request.get("expectedDate")));
        if (request.containsKey("paidAmount")) order.setPaidAmount(new java.math.BigDecimal(request.get("paidAmount").toString()));
        if (request.containsKey("remark")) order.setRemark((String) request.get("remark"));
        if (request.containsKey("operator")) order.setOperator((String) request.get("operator"));
        if (request.containsKey("status")) {
            String statusStr = (String) request.get("status");
            order.setStatus(PurchaseOrder.OrderStatus.valueOf(statusStr));
        }
        return order;
    }

    @SuppressWarnings("unchecked")
    private List<PurchaseOrderItem> buildItemsFromRequest(Map<String, Object> request) {
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
        if (itemsData == null) {
            throw new IllegalArgumentException("items 不能为空");
        }
        return itemsData.stream().map(itemData -> {
            PurchaseOrderItem item = new PurchaseOrderItem();
            if (itemData.containsKey("productId")) item.setProductId(parseLong(itemData.get("productId")));
            if (itemData.containsKey("productName")) item.setProductName((String) itemData.get("productName"));
            if (itemData.containsKey("productCode")) item.setProductCode((String) itemData.get("productCode"));
            if (itemData.containsKey("colorId")) item.setColorId(parseLong(itemData.get("colorId")));
            if (itemData.containsKey("colorName")) item.setColorName((String) itemData.get("colorName"));
            if (itemData.containsKey("colorCode")) item.setColorCode((String) itemData.get("colorCode"));
            if (itemData.containsKey("batchCode")) item.setBatchCode((String) itemData.get("batchCode"));
            if (itemData.containsKey("quantity")) item.setQuantity(new java.math.BigDecimal(itemData.get("quantity").toString()));
            if (itemData.containsKey("unit")) item.setUnit((String) itemData.get("unit"));
            if (itemData.containsKey("price")) item.setPrice(new java.math.BigDecimal(itemData.get("price").toString()));
            if (itemData.containsKey("productionDate")) item.setProductionDate(LocalDate.parse((String) itemData.get("productionDate")));
            if (itemData.containsKey("stockLocation")) item.setStockLocation((String) itemData.get("stockLocation"));
            if (itemData.containsKey("remark")) item.setRemark((String) itemData.get("remark"));
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
                throw new IllegalArgumentException("ID 参数格式错误", e);
            }
        }
        throw new IllegalArgumentException("不支持的 ID 类型: " + value.getClass());
    }
}
