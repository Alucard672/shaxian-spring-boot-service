package com.shaxian.appservice.purchase;

import com.shaxian.entity.PurchaseOrder;
import com.shaxian.entity.PurchaseOrderItem;
import com.shaxian.service.purchase.PurchaseService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    public PurchaseOrder createPurchase(com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest request) {
        PurchaseOrder order = buildOrderFromRequest(request);
        List<PurchaseOrderItem> items = buildItemsFromRequest(request);
        return purchaseService.createPurchase(order, items);
    }

    public PurchaseOrder updatePurchase(Long id, com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest request) {
        PurchaseOrder order = buildOrderFromRequest(request);
        List<PurchaseOrderItem> items = buildItemsFromRequest(request);
        return purchaseService.updatePurchase(id, order, items);
    }

    public void deletePurchase(Long id) {
        purchaseService.deletePurchase(id);
    }

    private PurchaseOrder buildOrderFromRequest(Object requestObj) {
        PurchaseOrder order = new PurchaseOrder();
        if (requestObj instanceof com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest) {
            com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest request = (com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest) requestObj;
            order.setSupplierId(request.getSupplierId());
            order.setSupplierName(request.getSupplierName());
            order.setPurchaseDate(request.getPurchaseDate());
            order.setExpectedDate(request.getExpectedDate());
            order.setRemark(request.getRemark());
            order.setOperator(request.getOperator());
            if (request.getStatus() != null) {
                order.setStatus(PurchaseOrder.OrderStatus.valueOf(request.getStatus()));
            }
        } else if (requestObj instanceof com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest) {
            com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest request = (com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest) requestObj;
            if (request.getSupplierName() != null) order.setSupplierName(request.getSupplierName());
            if (request.getPurchaseDate() != null) order.setPurchaseDate(request.getPurchaseDate());
            if (request.getExpectedDate() != null) order.setExpectedDate(request.getExpectedDate());
            if (request.getRemark() != null) order.setRemark(request.getRemark());
            if (request.getOperator() != null) order.setOperator(request.getOperator());
            if (request.getStatus() != null) {
                order.setStatus(PurchaseOrder.OrderStatus.valueOf(request.getStatus()));
            }
        }
        return order;
    }

    private List<PurchaseOrderItem> buildItemsFromRequest(Object requestObj) {
        java.util.List<com.shaxian.dto.purchase.request.PurchaseOrderItemRequest> itemsData = null;
        if (requestObj instanceof com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest) {
            itemsData = ((com.shaxian.dto.purchase.request.CreatePurchaseOrderRequest) requestObj).getItems();
        } else if (requestObj instanceof com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest) {
            itemsData = ((com.shaxian.dto.purchase.request.UpdatePurchaseOrderRequest) requestObj).getItems();
        }
        if (itemsData == null || itemsData.isEmpty()) {
            throw new IllegalArgumentException("items 不能为空");
        }
        return itemsData.stream().map(itemRequest -> {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setProductCode(itemRequest.getProductCode());
            item.setColorId(itemRequest.getColorId());
            item.setColorName(itemRequest.getColorName());
            item.setColorCode(itemRequest.getColorCode());
            item.setBatchCode(itemRequest.getBatchCode());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnit(itemRequest.getUnit());
            item.setPrice(itemRequest.getPrice());
            item.setProductionDate(itemRequest.getProductionDate());
            item.setStockLocation(itemRequest.getStockLocation());
            item.setRemark(itemRequest.getRemark());
            return item;
        }).toList();
    }
}
