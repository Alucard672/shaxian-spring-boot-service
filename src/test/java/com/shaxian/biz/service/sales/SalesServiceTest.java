package com.shaxian.biz.service.sales;

import com.shaxian.biz.entity.Batch;
import com.shaxian.biz.entity.SalesOrder;
import com.shaxian.biz.entity.SalesOrderItem;
import com.shaxian.biz.entity.SystemParams;
import com.shaxian.biz.repository.BatchRepository;
import com.shaxian.biz.repository.SalesOrderItemRepository;
import com.shaxian.biz.repository.SalesOrderRepository;
import com.shaxian.biz.service.settings.SystemParamsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 销售服务单元测试（库存扣减与校验）
 */
@ExtendWith(MockitoExtension.class)
class SalesServiceTest {

    @Mock
    private SalesOrderRepository salesOrderRepository;
    @Mock
    private SalesOrderItemRepository salesOrderItemRepository;
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private SystemParamsService systemParamsService;

    private SalesService salesService;

    @BeforeEach
    void setUp() {
        salesService = new SalesService(salesOrderRepository, salesOrderItemRepository, batchRepository, systemParamsService);
    }

    private void mockSystemParamsAllowNegativeStock(boolean allow) {
        SystemParams params = new SystemParams();
        params.setAllowNegativeStock(allow);
        when(systemParamsService.getSystemParams()).thenReturn(params);
    }

    @Test
    void createSales_statusShipped_withBatchId_decreasesStock() {
        mockSystemParamsAllowNegativeStock(false);
        SalesOrder order = new SalesOrder();
        order.setTenantId(1L);
        order.setCustomerId(1L);
        order.setCustomerName("客户");
        order.setSalesDate(LocalDate.now());
        order.setStatus(SalesOrder.OrderStatus.SHIPPED);
        order.setReceivedAmount(BigDecimal.ZERO);

        SalesOrderItem item = new SalesOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(1L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchId(10L);
        item.setBatchCode("B001");
        item.setQuantity(new BigDecimal("5"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("100"));

        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> {
            SalesOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        Batch batch = new Batch();
        batch.setId(10L);
        batch.setCode("B001");
        batch.setStockQuantity(new BigDecimal("10"));
        when(batchRepository.findById(10L)).thenReturn(Optional.of(batch));

        SalesOrder result = salesService.createSales(order, List.of(item));

        verify(salesOrderRepository).save(any(SalesOrder.class));
        verify(salesOrderItemRepository).saveAll(anyList());
        verify(batchRepository).findById(10L);
        verify(batchRepository).decreaseStock(eq(10L), eq(new BigDecimal("5")));
    }

    @Test
    void createSales_statusDraft_doesNotDecreaseStock() {
        SalesOrder order = new SalesOrder();
        order.setTenantId(1L);
        order.setCustomerId(1L);
        order.setCustomerName("客户");
        order.setSalesDate(LocalDate.now());
        order.setStatus(SalesOrder.OrderStatus.DRAFT);
        order.setReceivedAmount(BigDecimal.ZERO);

        SalesOrderItem item = new SalesOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(1L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchId(10L);
        item.setBatchCode("B001");
        item.setQuantity(new BigDecimal("5"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("100"));

        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> {
            SalesOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        salesService.createSales(order, List.of(item));

        verify(salesOrderRepository).save(any(SalesOrder.class));
        verify(salesOrderItemRepository).saveAll(anyList());
        verify(batchRepository, never()).findById(anyLong());
        verify(batchRepository, never()).decreaseStock(anyLong(), any(BigDecimal.class));
    }

    @Test
    void createSales_statusShipped_insufficientStock_throwsAndDoesNotDecrease() {
        mockSystemParamsAllowNegativeStock(false);
        SalesOrder order = new SalesOrder();
        order.setTenantId(1L);
        order.setCustomerId(1L);
        order.setCustomerName("客户");
        order.setSalesDate(LocalDate.now());
        order.setStatus(SalesOrder.OrderStatus.SHIPPED);
        order.setReceivedAmount(BigDecimal.ZERO);

        SalesOrderItem item = new SalesOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(1L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchId(10L);
        item.setBatchCode("B001");
        item.setQuantity(new BigDecimal("20"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("100"));

        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> {
            SalesOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        Batch batch = new Batch();
        batch.setId(10L);
        batch.setCode("B001");
        batch.setStockQuantity(new BigDecimal("10"));
        when(batchRepository.findById(10L)).thenReturn(Optional.of(batch));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> salesService.createSales(order, List.of(item)));
        assertTrue(ex.getMessage().contains("缸号库存不足"));

        verify(batchRepository).findById(10L);
        verify(batchRepository, never()).decreaseStock(anyLong(), any(BigDecimal.class));
    }

    @Test
    void createSales_statusShipped_allowNegativeStock_insufficientStock_decreasesStock() {
        mockSystemParamsAllowNegativeStock(true);
        SalesOrder order = new SalesOrder();
        order.setTenantId(1L);
        order.setCustomerId(1L);
        order.setCustomerName("客户");
        order.setSalesDate(LocalDate.now());
        order.setStatus(SalesOrder.OrderStatus.SHIPPED);
        order.setReceivedAmount(BigDecimal.ZERO);

        SalesOrderItem item = new SalesOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(1L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchId(10L);
        item.setBatchCode("B001");
        item.setQuantity(new BigDecimal("20"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("100"));

        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> {
            SalesOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        SalesOrder result = salesService.createSales(order, List.of(item));

        verify(salesOrderRepository).save(any(SalesOrder.class));
        verify(salesOrderItemRepository).saveAll(anyList());
        verify(batchRepository, never()).findById(anyLong());
        verify(batchRepository).decreaseStock(eq(10L), eq(new BigDecimal("20")));
    }

    @Test
    void createSales_statusShipped_batchIdNull_doesNotDecreaseStock() {
        mockSystemParamsAllowNegativeStock(false);
        SalesOrder order = new SalesOrder();
        order.setTenantId(1L);
        order.setCustomerId(1L);
        order.setCustomerName("客户");
        order.setSalesDate(LocalDate.now());
        order.setStatus(SalesOrder.OrderStatus.SHIPPED);
        order.setReceivedAmount(BigDecimal.ZERO);

        SalesOrderItem item = new SalesOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(1L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchId(null);
        item.setBatchCode("");
        item.setQuantity(new BigDecimal("5"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("100"));

        when(salesOrderRepository.save(any(SalesOrder.class))).thenAnswer(inv -> {
            SalesOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        salesService.createSales(order, List.of(item));

        verify(batchRepository, never()).findById(anyLong());
        verify(batchRepository, never()).decreaseStock(anyLong(), any(BigDecimal.class));
    }
}
