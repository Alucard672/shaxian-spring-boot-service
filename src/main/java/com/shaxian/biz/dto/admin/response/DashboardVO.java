package com.shaxian.biz.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "管理端 Dashboard 数据")
public class DashboardVO {
    private long totalTenants;
    private long activeTenants;
    private List<TenantVO> expiringIn30Days;
    private List<TenantVO> expired;
}
