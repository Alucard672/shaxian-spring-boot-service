package com.shaxian.biz.service.purchase;

import com.shaxian.biz.entity.Batch;
import com.shaxian.biz.entity.PurchaseOrder;
import com.shaxian.biz.entity.PurchaseOrderItem;
import com.shaxian.biz.repository.BatchRepository;
import com.shaxian.biz.repository.PurchaseOrderItemRepository;
import com.shaxian.biz.repository.PurchaseOrderRepository;
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
 * 采购服务单元测试（入库更新库存）
 */
@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    private PurchaseOrderRepository purchaseOrderRepository;
    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepository;
    @Mock
    private BatchRepository batchRepository;

    private PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        purchaseService = new PurchaseService(purchaseOrderRepository, purchaseOrderItemRepository, batchRepository);
    }

    @Test
    void createPurchase_statusReceived_batchExists_increasesStock() {
        PurchaseOrder order = new PurchaseOrder();
        order.setTenantId(1L);
        order.setSupplierId(1L);
        order.setSupplierName("供应商");
        order.setPurchaseDate(LocalDate.now());
        order.setStatus(PurchaseOrder.OrderStatus.RECEIVED);
        order.setPaidAmount(BigDecimal.ZERO);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(5L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchCode("BATCH-001");
        item.setQuantity(new BigDecimal("100"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("10"));

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> {
            PurchaseOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        Batch existingBatch = new Batch();
        existingBatch.setId(20L);
        existingBatch.setCode("BATCH-001");
        existingBatch.setTenantId(1L);
        existingBatch.setColorId(5L);
        when(batchRepository.findByTenantIdAndCode(1L, "BATCH-001")).thenReturn(Optional.of(existingBatch));

        PurchaseOrder result = purchaseService.createPurchase(order, List.of(item));

        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verify(purchaseOrderItemRepository).saveAll(anyList());
        verify(batchRepository).findByTenantIdAndCode(1L, "BATCH-001");
        verify(batchRepository).increaseStock(eq(20L), eq(new BigDecimal("100")));
        verify(batchRepository, never()).save(any(Batch.class));
    }

    @Test
    void createPurchase_statusReceived_batchNotExists_createsBatchAndNoIncreaseStock() {
        PurchaseOrder order = new PurchaseOrder();
        order.setTenantId(1L);
        order.setSupplierId(1L);
        order.setSupplierName("供应商");
        order.setPurchaseDate(LocalDate.now());
        order.setStatus(PurchaseOrder.OrderStatus.RECEIVED);
        order.setPaidAmount(BigDecimal.ZERO);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(5L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchCode("BATCH-NEW");
        item.setQuantity(new BigDecimal("50"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("20"));

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> {
            PurchaseOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });
        when(batchRepository.findByTenantIdAndCode(1L, "BATCH-NEW")).thenReturn(Optional.empty());
        when(batchRepository.save(any(Batch.class))).thenAnswer(inv -> {
            Batch b = inv.getArgument(0);
            b.setId(30L);
            return b;
        });

        PurchaseOrder result = purchaseService.createPurchase(order, List.of(item));

        verify(batchRepository).findByTenantIdAndCode(1L, "BATCH-NEW");
        ArgumentCaptor<Batch> batchCaptor = ArgumentCaptor.forClass(Batch.class);
        verify(batchRepository).save(batchCaptor.capture());
        Batch saved = batchCaptor.getValue();
        assertEquals(1L, saved.getTenantId());
        assertEquals(5L, saved.getColorId());
        assertEquals("BATCH-NEW", saved.getCode());
        assertEquals(0, new BigDecimal("50").compareTo(saved.getStockQuantity()));
        assertEquals(0, new BigDecimal("50").compareTo(saved.getInitialQuantity()));
        verify(batchRepository, never()).increaseStock(anyLong(), any(BigDecimal.class));
    }

    @Test
    void createPurchase_statusDraft_doesNotTouchBatch() {
        PurchaseOrder order = new PurchaseOrder();
        order.setTenantId(1L);
        order.setSupplierId(1L);
        order.setSupplierName("供应商");
        order.setPurchaseDate(LocalDate.now());
        order.setStatus(PurchaseOrder.OrderStatus.DRAFT);
        order.setPaidAmount(BigDecimal.ZERO);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(5L);
        item.setBatchCode("BATCH-001");
        item.setQuantity(new BigDecimal("100"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("10"));

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> {
            PurchaseOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        purchaseService.createPurchase(order, List.of(item));

        verify(purchaseOrderRepository).save(any(PurchaseOrder.class));
        verify(purchaseOrderItemRepository).saveAll(anyList());
        verify(batchRepository, never()).findByTenantIdAndCode(anyLong(), any());
        verify(batchRepository, never()).increaseStock(anyLong(), any(BigDecimal.class));
        verify(batchRepository, never()).save(any(Batch.class));
    }

    @Test
    void updatePurchase_draftToReceived_increasesStock() {
        PurchaseOrder existing = new PurchaseOrder();
        existing.setId(1L);
        existing.setTenantId(1L);
        existing.setOrderNumber("PO001");
        existing.setSupplierId(1L);
        existing.setSupplierName("供应商");
        existing.setPurchaseDate(LocalDate.now());
        existing.setStatus(PurchaseOrder.OrderStatus.DRAFT);
        existing.setCreatedAt(java.time.LocalDateTime.now());

        PurchaseOrder order = new PurchaseOrder();
        order.setTenantId(1L);
        order.setSupplierId(1L);
        order.setSupplierName("供应商");
        order.setPurchaseDate(LocalDate.now());
        order.setStatus(PurchaseOrder.OrderStatus.RECEIVED);
        order.setPaidAmount(BigDecimal.ZERO);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setTenantId(1L);
        item.setOrderId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(5L);
        item.setColorName("色");
        item.setColorCode("C1");
        item.setBatchCode("BATCH-002");
        item.setQuantity(new BigDecimal("80"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("15"));

        when(purchaseOrderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> inv.getArgument(0));
        Batch existingBatch = new Batch();
        existingBatch.setId(25L);
        existingBatch.setCode("BATCH-002");
        when(batchRepository.findByTenantIdAndCode(1L, "BATCH-002")).thenReturn(Optional.of(existingBatch));

        purchaseService.updatePurchase(1L, order, List.of(item));

        verify(purchaseOrderItemRepository).deleteByOrderId(1L);
        verify(purchaseOrderItemRepository).saveAll(anyList());
        verify(batchRepository).findByTenantIdAndCode(1L, "BATCH-002");
        verify(batchRepository).increaseStock(eq(25L), eq(new BigDecimal("80")));
    }

    @Test
    void createPurchase_statusReceived_itemWithoutColorId_throws() {
        PurchaseOrder order = new PurchaseOrder();
        order.setTenantId(1L);
        order.setSupplierId(1L);
        order.setSupplierName("供应商");
        order.setPurchaseDate(LocalDate.now());
        order.setStatus(PurchaseOrder.OrderStatus.RECEIVED);
        order.setPaidAmount(BigDecimal.ZERO);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setTenantId(1L);
        item.setProductId(1L);
        item.setProductName("商品");
        item.setProductCode("P001");
        item.setColorId(null);
        item.setBatchCode("BATCH-001");
        item.setQuantity(new BigDecimal("100"));
        item.setUnit("kg");
        item.setPrice(new BigDecimal("10"));

        when(purchaseOrderRepository.save(any(PurchaseOrder.class))).thenAnswer(inv -> {
            PurchaseOrder o = inv.getArgument(0);
            o.setId(1L);
            return o;
        });

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> purchaseService.createPurchase(order, List.of(item)));
        assertTrue(ex.getMessage().contains("必须指定色号") || ex.getMessage().contains("color_id"));
    }
}
