package com.shaxian.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.TenantId;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "adjustment_orders", indexes = @Index(name = "idx_tenant_id", columnList = "tenant_id"))
public class AdjustmentOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @TenantId
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AdjustmentType type;

    @Column(name = "adjustment_date", nullable = false)
    private LocalDate adjustmentDate;

    @Column(name = "total_quantity", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalQuantity = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.DRAFT;

    @Column(nullable = false, length = 100)
    private String operator;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "orderId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdjustmentOrderItem> items;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AdjustmentType {
        INCREASE, DECREASE, SURPLUS, DEFICIT, LOSS, OTHER
    }

    public enum OrderStatus {
        DRAFT, COMPLETED
    }
}
