package com.shaxian.biz.dto.admin.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Schema(description = "租户详情（含订阅记录 + 活跃 session）")
public class TenantDetailVO extends TenantVO {
    private List<SubscriptionVO> subscriptions;
    private List<ActiveSessionVO> activeSessions;
}
