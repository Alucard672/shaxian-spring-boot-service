package com.shaxian.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "crm_customers", indexes = @Index(name = "idx_phone", columnList = "phone"))
public class CrmCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(nullable = false, unique = true, length = 50)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerSource source;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerType type = CustomerType.POTENTIAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum CustomerSource {
        ONLINE,      // 线上
        OFFLINE,     // 线下
        REFERRAL,    // 转介绍
        EXHIBITION,  // 展会
        ADVERTISING, // 广告
        OTHER        // 其他
    }

    public enum CustomerType {
        OFFICIAL,    // 正式客户
        POTENTIAL    // 潜在客户
    }
}

