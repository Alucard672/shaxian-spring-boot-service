package com.shaxian.biz.controller;

import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.entity.DyeingOrder;
import com.shaxian.biz.entity.DyeingOrderItem;
import com.shaxian.biz.repository.DyeingOrderItemRepository;
import com.shaxian.biz.repository.DyeingOrderRepository;
import com.shaxian.biz.util.OrderNumberGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/dyeing")
@Tag(name = "染色管理", description = "染色加工单管理接口")
public class DyeingController {
    private final DyeingOrderRepository dyeingOrderRepository;
    private final DyeingOrderItemRepository dyeingOrderItemRepository;

    public DyeingController(
            DyeingOrderRepository dyeingOrderRepository,
            DyeingOrderItemRepository dyeingOrderItemRepository) {
        this.dyeingOrderRepository = dyeingOrderRepository;
        this.dyeingOrderItemRepository = dyeingOrderItemRepository;
    }


    @GetMapping
    @Operation(summary = "获取染色加工单列表", description = "查询染色加工单，支持按状态和商品ID筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取染色加工单列表")
    })
    public ResponseEntity<List<DyeingOrder>> getAllDyeingOrders(
            @RequestParam(required = false) Integer pageNo,
            @RequestParam(required = false) Integer pageSize,
            @RequestBody(required = false) com.shaxian.biz.dto.dyeing.request.DyeingOrderQueryRequest request,
            UserSession session) {
        String status = request != null ? request.getStatus() : null;
        Long productId = request != null ? request.getProductId() : null;
        List<DyeingOrder> orders = dyeingOrderRepository.findByFilters(status, productId);
        orders.forEach(order -> order.setItems(dyeingOrderItemRepository.findByOrderId(order.getId())));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取染色加工单详情", description = "根据ID获取染色加工单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取染色加工单信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "染色加工单不存在")
    })
    public ResponseEntity<DyeingOrder> getDyeingOrder(
            @Parameter(description = "染色加工单ID", required = true) @PathVariable Long id,
            UserSession session) {
        Optional<DyeingOrder> order = dyeingOrderRepository.findById(id);
        if (order.isPresent()) {
            order.get().setItems(dyeingOrderItemRepository.findByOrderId(id));
            return ResponseEntity.ok(order.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "创建染色加工单", description = "创建新的染色加工单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建染色加工单"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> createDyeingOrder(
            @RequestBody com.shaxian.biz.dto.dyeing.request.CreateDyeingOrderRequest request,
            UserSession session) {
        try {
            DyeingOrder order = new DyeingOrder();
            order.setOrderNumber(OrderNumberGenerator.generateDyeingOrderNumber());
            order.setProductId(request.getProductId());
            order.setProductName(request.getProductName());
            order.setGreyBatchId(request.getGreyBatchId());
            order.setGreyBatchCode(request.getGreyBatchCode());
            order.setFactoryId(request.getFactoryId());
            order.setFactoryName(request.getFactoryName());
            order.setFactoryPhone(request.getFactoryPhone());
            order.setShipmentDate(request.getShipmentDate());
            order.setExpectedCompletionDate(request.getExpectedCompletionDate());
            order.setProcessingPrice(request.getProcessingPrice());
            order.setRemark(request.getRemark());
            order.setOperator(request.getOperator());
            if (request.getStatus() != null) {
                order.setStatus(DyeingOrder.OrderStatus.valueOf(request.getStatus()));
            }
            
            BigDecimal totalQuantity = request.getItems().stream()
                    .map(item -> item.getQuantity())
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(totalQuantity.multiply(order.getProcessingPrice()));
            
            DyeingOrder saved = dyeingOrderRepository.save(order);
            
            List<DyeingOrderItem> items = request.getItems().stream().map(itemRequest -> {
                DyeingOrderItem item = new DyeingOrderItem();
                item.setOrderId(saved.getId());
                item.setTargetColorId(itemRequest.getTargetColorId());
                item.setTargetColorCode(itemRequest.getTargetColorCode());
                item.setTargetColorName(itemRequest.getTargetColorName());
                item.setTargetColorValue(itemRequest.getTargetColorValue());
                item.setQuantity(itemRequest.getQuantity());
                return item;
            }).toList();
            
            dyeingOrderItemRepository.saveAll(items);
            saved.setItems(items);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新染色加工单", description = "更新染色加工单信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新染色加工单"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    public ResponseEntity<?> updateDyeingOrder(
            @Parameter(description = "染色加工单ID", required = true) @PathVariable Long id,
            @RequestBody com.shaxian.biz.dto.dyeing.request.UpdateDyeingOrderRequest request,
            UserSession session) {
        try {
            DyeingOrder order = dyeingOrderRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("染色加工单不存在或无权访问"));
            
            if (request.getProductId() != null) order.setProductId(request.getProductId());
            if (request.getProductName() != null) order.setProductName(request.getProductName());
            if (request.getGreyBatchId() != null) order.setGreyBatchId(request.getGreyBatchId());
            if (request.getGreyBatchCode() != null) order.setGreyBatchCode(request.getGreyBatchCode());
            if (request.getFactoryId() != null) order.setFactoryId(request.getFactoryId());
            if (request.getFactoryName() != null) order.setFactoryName(request.getFactoryName());
            if (request.getFactoryPhone() != null) order.setFactoryPhone(request.getFactoryPhone());
            if (request.getShipmentDate() != null) order.setShipmentDate(request.getShipmentDate());
            if (request.getExpectedCompletionDate() != null) order.setExpectedCompletionDate(request.getExpectedCompletionDate());
            if (request.getActualCompletionDate() != null) order.setActualCompletionDate(request.getActualCompletionDate());
            if (request.getProcessingPrice() != null) order.setProcessingPrice(request.getProcessingPrice());
            if (request.getRemark() != null) order.setRemark(request.getRemark());
            if (request.getOperator() != null) order.setOperator(request.getOperator());
            if (request.getStatus() != null) {
                order.setStatus(DyeingOrder.OrderStatus.valueOf(request.getStatus()));
            }
            
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                BigDecimal totalQuantity = request.getItems().stream()
                        .map(item -> item.getQuantity())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                order.setTotalAmount(totalQuantity.multiply(order.getProcessingPrice()));
                
                dyeingOrderItemRepository.deleteByOrderId(id);
                
                List<DyeingOrderItem> items = request.getItems().stream().map(itemRequest -> {
                    DyeingOrderItem item = new DyeingOrderItem();
                    item.setOrderId(id);
                    item.setTargetColorId(itemRequest.getTargetColorId());
                    item.setTargetColorCode(itemRequest.getTargetColorCode());
                    item.setTargetColorName(itemRequest.getTargetColorName());
                    item.setTargetColorValue(itemRequest.getTargetColorValue());
                    item.setQuantity(itemRequest.getQuantity());
                    return item;
                }).toList();
                
                dyeingOrderItemRepository.saveAll(items);
                order.setItems(items);
            }
            
            DyeingOrder saved = dyeingOrderRepository.save(order);
            
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除染色加工单", description = "删除指定染色加工单")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除染色加工单"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "染色加工单不存在")
    })
    public ResponseEntity<Void> deleteDyeingOrder(
            @Parameter(description = "染色加工单ID", required = true) @PathVariable Long id,
            UserSession session) {
        if (!dyeingOrderRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        dyeingOrderRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}

