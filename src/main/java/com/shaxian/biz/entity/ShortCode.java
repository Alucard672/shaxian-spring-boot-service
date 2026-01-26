package com.shaxian.biz.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 短码实体
 * 用于存储短码与原始分享码的映射关系
 * 注意：此表不区分租户，所有租户的短码数据统一存储
 */
@Data
@Entity
@Table(name = "short_codes", indexes = @Index(name = "idx_short_code", columnList = "short_code"))
public class ShortCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "short_code", nullable = false, unique = true, length = 6)
    private String shortCode;

    @Column(name = "original_code", nullable = false, columnDefinition = "TEXT")
    private String originalCode;

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
}
