package com.shaxian.crm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "crm_roles", indexes = {
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_level", columnList = "level")
})
public class CrmRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false)
    private Integer level = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleStatus status = RoleStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String description;

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

    public enum RoleStatus {
        ACTIVE,    // 启用
        INACTIVE   // 停用
    }
}
