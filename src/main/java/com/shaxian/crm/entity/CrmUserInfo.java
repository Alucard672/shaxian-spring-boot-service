package com.shaxian.crm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonIgnore // 序列化时忽略持久化字段，使用@Transient的roleIds字段
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

    /**
     * 角色ID列表（用于返回给前端，不持久化）
     * 注意：与持久化字段roleIds（String类型）不同，此字段为List<Long>类型
     */
    @Transient
    private List<Long> roleIdList;

    /**
     * 角色名称列表（用于返回给前端，不持久化）
     */
    @Transient
    private List<String> roleNames;

    /**
     * 获取角色ID列表（用于JSON序列化，字段名为roleIds）
     */
    @JsonProperty("roleIds")
    public List<Long> getRoleIds() {
        return roleIdList != null ? roleIdList : getRoleIdsList();
    }

    /**
     * 设置角色ID列表（用于JSON反序列化，字段名为roleIds）
     * 注意：此方法仅用于设置临时字段，不会修改持久化字段
     */
    @JsonProperty("roleIds")
    public void setRoleIds(List<Long> roleIds) {
        this.roleIdList = roleIds;
    }

    /**
     * 获取角色名称列表
     */
    @JsonProperty("roleNames")
    public List<String> getRoleNames() {
        return roleNames != null ? roleNames : new ArrayList<>();
    }

    /**
     * 设置角色名称列表
     */
    @JsonProperty("roleNames")
    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
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

