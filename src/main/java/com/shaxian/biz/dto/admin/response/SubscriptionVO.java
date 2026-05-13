package com.shaxian.biz.dto.admin.response;

import com.shaxian.biz.entity.Subscription;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "订阅 / 续费记录视图")
public class SubscriptionVO {
    private Long id;
    private Long tenantId;
    private BigDecimal amount;
    private LocalDateTime prevExpiresAt;
    private LocalDateTime newExpiresAt;
    private Long operatorUserId;
    private String operatorUserName;
    private String note;
    private LocalDateTime createdAt;

    public static SubscriptionVO from(Subscription s, String operatorUserName) {
        SubscriptionVO v = new SubscriptionVO();
        v.setId(s.getId());
        v.setTenantId(s.getTenantId());
        v.setAmount(s.getAmount());
        v.setPrevExpiresAt(s.getPrevExpiresAt());
        v.setNewExpiresAt(s.getNewExpiresAt());
        v.setOperatorUserId(s.getOperatorUserId());
        v.setOperatorUserName(operatorUserName);
        v.setNote(s.getNote());
        v.setCreatedAt(s.getCreatedAt());
        return v;
    }
}
