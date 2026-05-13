package com.shaxian.biz.dto.admin.response;

import com.shaxian.biz.entity.Tenant;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Schema(description = "租户视图")
public class TenantVO {
    private Long id;
    private String name;
    private String code;
    private String address;
    private LocalDateTime expiresAt;
    private Tenant.TenantStatus status;
    private Long packageId;
    private String packageName;
    private Integer packageConcurrentLimit;
    private Long assignedUserId;
    private String assignedUserName;
    /**
     * 距离到期剩余天数（可负）；expiresAt 为 null 时为 null
     */
    private Long remainingDays;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TenantVO from(Tenant t) {
        TenantVO v = new TenantVO();
        v.setId(t.getId());
        v.setName(t.getName());
        v.setCode(t.getCode());
        v.setAddress(t.getAddress());
        v.setExpiresAt(t.getExpiresAt());
        v.setStatus(t.getStatus());
        v.setPackageId(t.getPackageId());
        v.setAssignedUserId(t.getAssignedUserId());
        v.setCreatedAt(t.getCreatedAt());
        v.setUpdatedAt(t.getUpdatedAt());
        if (t.getExpiresAt() != null) {
            v.setRemainingDays(ChronoUnit.DAYS.between(LocalDateTime.now(), t.getExpiresAt()));
        }
        return v;
    }
}
