package com.shaxian.biz.appservice.sales;

import com.shaxian.biz.entity.SalesOrder;
import com.shaxian.biz.entity.SalesOrderItem;
import com.shaxian.biz.service.sales.SalesService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class SalesAppService {

    private final SalesService salesService;

    public SalesAppService(SalesService salesService) {
        this.salesService = salesService;
    }

    public List<SalesOrder> listSales(String status, Long customerId, LocalDate startDate, LocalDate endDate) {
        List<SalesOrder> orders = salesService.getAllSales(status, customerId, startDate, endDate);
        orders.forEach(order -> salesService.getSalesById(order.getId())
                .ifPresent(full -> order.setItems(full.getItems())));
        return orders;
    }

    public Optional<SalesOrder> findById(Long id) {
        return salesService.getSalesById(id);
    }

    public SalesOrder createSales(com.shaxian.biz.dto.sales.request.CreateSalesOrderRequest request) {
        SalesOrder order = buildOrderFromRequest(request);
        List<SalesOrderItem> items = buildItemsFromRequest(request);
        return salesService.createSales(order, items);
    }

    public SalesOrder updateSales(Long id, com.shaxian.biz.dto.sales.request.UpdateSalesOrderRequest request) {
        SalesOrder order = buildOrderFromRequest(request);
        List<SalesOrderItem> items = buildItemsFromRequest(request);
        return salesService.updateSales(id, order, items);
    }

    public void deleteSales(Long id) {
        salesService.deleteSales(id);
    }

    private SalesOrder buildOrderFromRequest(Object requestObj) {
        SalesOrder order = new SalesOrder();
        if (requestObj instanceof com.shaxian.biz.dto.sales.request.CreateSalesOrderRequest) {
            com.shaxian.biz.dto.sales.request.CreateSalesOrderRequest request = (com.shaxian.biz.dto.sales.request.CreateSalesOrderRequest) requestObj;
            order.setCustomerId(request.getCustomerId());
            order.setCustomerName(request.getCustomerName());
            order.setSalesDate(request.getSalesDate());
            order.setExpectedDate(request.getExpectedDate());
            order.setRemark(request.getRemark());
            order.setOperator(request.getOperator());
            if (request.getStatus() != null) {
                order.setStatus(SalesOrder.OrderStatus.valueOf(request.getStatus()));
            }
        } else if (requestObj instanceof com.shaxian.biz.dto.sales.request.UpdateSalesOrderRequest) {
            com.shaxian.biz.dto.sales.request.UpdateSalesOrderRequest request = (com.shaxian.biz.dto.sales.request.UpdateSalesOrderRequest) requestObj;
            if (request.getCustomerName() != null) order.setCustomerName(request.getCustomerName());
            if (request.getSalesDate() != null) order.setSalesDate(request.getSalesDate());
            if (request.getExpectedDate() != null) order.setExpectedDate(request.getExpectedDate());
            if (request.getRemark() != null) order.setRemark(request.getRemark());
            if (request.getOperator() != null) order.setOperator(request.getOperator());
            if (request.getStatus() != null) {
                order.setStatus(SalesOrder.OrderStatus.valueOf(request.getStatus()));
            }
        }
        return order;
    }

    private List<SalesOrderItem> buildItemsFromRequest(Object requestObj) {
        java.util.List<com.shaxian.biz.dto.sales.request.SalesOrderItemRequest> itemsData = null;
        if (requestObj instanceof com.shaxian.biz.dto.sales.request.CreateSalesOrderRequest) {
            itemsData = ((com.shaxian.biz.dto.sales.request.CreateSalesOrderRequest) requestObj).getItems();
        } else if (requestObj instanceof com.shaxian.biz.dto.sales.request.UpdateSalesOrderRequest) {
            itemsData = ((com.shaxian.biz.dto.sales.request.UpdateSalesOrderRequest) requestObj).getItems();
        }
        if (itemsData == null || itemsData.isEmpty()) {
            throw new IllegalArgumentException("items 不能为空");
        }
        return itemsData.stream().map(itemRequest -> {
            SalesOrderItem item = new SalesOrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setProductCode(itemRequest.getProductCode());
            item.setColorId(itemRequest.getColorId());
            item.setColorName(itemRequest.getColorName());
            item.setColorCode(itemRequest.getColorCode());
            // batch_id、batch_code 非必填，未传时保持 null/空
            item.setBatchId(itemRequest.getBatchId());
            item.setBatchCode(itemRequest.getBatchCode() != null && !itemRequest.getBatchCode().isEmpty()
                    ? itemRequest.getBatchCode() : "");
            item.setQuantity(itemRequest.getQuantity());
            item.setUnit(itemRequest.getUnit());
            item.setPrice(itemRequest.getPrice());
            item.setRemark(itemRequest.getRemark());
            return item;
        }).toList();
    }
}
