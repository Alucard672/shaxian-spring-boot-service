package com.shaxian.biz.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.TenantId;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products", indexes = @Index(name = "idx_tenant_id", columnList = "tenant_id"))
@Schema(description = "商品实体")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "商品ID", example = "1")
    private Long id;

    @TenantId
    @Column(name = "tenant_id", nullable = false)
    @Schema(description = "租户ID")
    private Long tenantId;

    @Column(nullable = false, length = 200)
    @Schema(description = "商品名称", example = "纯棉纱线")
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "商品编码", example = "PROD001")
    private String code;

    @Column(length = 100)
    @Schema(description = "规格", example = "40支")
    private String specification;

    @Column(length = 200)
    @Schema(description = "成分", example = "100%纯棉")
    private String composition;

    @Column(length = 50)
    @Schema(description = "支数", example = "40")
    private String count;

    @Column(nullable = false, length = 20)
    @Schema(description = "单位", example = "kg")
    private String unit = "kg";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Schema(description = "商品类型", example = "RAW_MATERIAL")
    private ProductType type = ProductType.RAW_MATERIAL;

    @Column(name = "is_white_yarn")
    @Schema(description = "是否为白纱", example = "false")
    private Boolean isWhiteYarn = false;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "描述", example = "优质纯棉纱线")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @Schema(description = "更新时间")
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

    public enum ProductType {
        RAW_MATERIAL, SEMI_FINISHED, FINISHED
    }
}

