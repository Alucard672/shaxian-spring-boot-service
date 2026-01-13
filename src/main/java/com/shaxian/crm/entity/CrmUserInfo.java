package com.shaxian.crm.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "crm_user_info", indexes = @Index(name = "idx_status", columnList = "status"))
public class CrmUserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String phone;

    @Column(length = 200)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(length = 200)
    private String email;

    @Column(name = "role_ids", nullable = false, columnDefinition = "TEXT")
    private String roleIds; // JSON数组格式，如 "[1,2,3]"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取角色ID列表
     */
    @Transient
    public List<Long> getRoleIdsList() {
        if (roleIds == null || roleIds.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(roleIds, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 设置角色ID列表
     */
    @Transient
    public void setRoleIdsList(List<Long> roleIdsList) {
        if (roleIdsList == null || roleIdsList.isEmpty()) {
            this.roleIds = "[]";
        } else {
            try {
                this.roleIds = objectMapper.writeValueAsString(roleIdsList);
            } catch (Exception e) {
                this.roleIds = "[]";
            }
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum UserStatus {
        ACTIVE,    // 启用
        INACTIVE   // 停用
    }
}

