package com.shaxian.appservice.sales;

import com.shaxian.entity.SalesOrder;
import com.shaxian.entity.SalesOrderItem;
import com.shaxian.service.SalesService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
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

    public SalesOrder createSales(Map<String, Object> request) {
        SalesOrder order = buildOrderFromRequest(request);
        List<SalesOrderItem> items = buildItemsFromRequest(request);
        return salesService.createSales(order, items);
    }

    public SalesOrder updateSales(Long id, Map<String, Object> request) {
        SalesOrder order = buildOrderFromRequest(request);
        List<SalesOrderItem> items = buildItemsFromRequest(request);
        return salesService.updateSales(id, order, items);
    }

    public void deleteSales(Long id) {
        salesService.deleteSales(id);
    }

    private SalesOrder buildOrderFromRequest(Map<String, Object> request) {
        SalesOrder order = new SalesOrder();
        if (request.containsKey("customerId")) order.setCustomerId(parseLong(request.get("customerId")));
        if (request.containsKey("customerName")) order.setCustomerName((String) request.get("customerName"));
        if (request.containsKey("salesDate")) order.setSalesDate(LocalDate.parse((String) request.get("salesDate")));
        if (request.containsKey("expectedDate")) order.setExpectedDate(LocalDate.parse((String) request.get("expectedDate")));
        if (request.containsKey("receivedAmount")) order.setReceivedAmount(new BigDecimal(request.get("receivedAmount").toString()));
        if (request.containsKey("remark")) order.setRemark((String) request.get("remark"));
        if (request.containsKey("operator")) order.setOperator((String) request.get("operator"));
        if (request.containsKey("status")) {
            String statusStr = (String) request.get("status");
            order.setStatus(SalesOrder.OrderStatus.valueOf(statusStr));
        }
        return order;
    }

    @SuppressWarnings("unchecked")
    private List<SalesOrderItem> buildItemsFromRequest(Map<String, Object> request) {
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) request.get("items");
        if (itemsData == null) {
            throw new IllegalArgumentException("items 不能为空");
        }
        return itemsData.stream().map(itemData -> {
            SalesOrderItem item = new SalesOrderItem();
            if (itemData.containsKey("productId")) item.setProductId(parseLong(itemData.get("productId")));
            if (itemData.containsKey("productName")) item.setProductName((String) itemData.get("productName"));
            if (itemData.containsKey("productCode")) item.setProductCode((String) itemData.get("productCode"));
            if (itemData.containsKey("colorId")) item.setColorId(parseLong(itemData.get("colorId")));
            if (itemData.containsKey("colorName")) item.setColorName((String) itemData.get("colorName"));
            if (itemData.containsKey("colorCode")) item.setColorCode((String) itemData.get("colorCode"));
            if (itemData.containsKey("batchId")) item.setBatchId(parseLong(itemData.get("batchId")));
            if (itemData.containsKey("batchCode")) item.setBatchCode((String) itemData.get("batchCode"));
            if (itemData.containsKey("quantity")) item.setQuantity(new BigDecimal(itemData.get("quantity").toString()));
            if (itemData.containsKey("unit")) item.setUnit((String) itemData.get("unit"));
            if (itemData.containsKey("price")) item.setPrice(new BigDecimal(itemData.get("price").toString()));
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
