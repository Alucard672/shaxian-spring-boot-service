package com.shaxian.biz.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "subscriptions", indexes = {
        @Index(name = "idx_subscriptions_tenant", columnList = "tenant_id"),
        @Index(name = "idx_subscriptions_operator", columnList = "operator_user_id")
})
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "prev_expires_at")
    private LocalDateTime prevExpiresAt;

    @Column(name = "new_expires_at", nullable = false)
    private LocalDateTime newExpiresAt;

    @Column(name = "operator_user_id", nullable = false)
    private Long operatorUserId;

    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
