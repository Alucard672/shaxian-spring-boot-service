package com.shaxian.biz.service.admin;

import com.shaxian.biz.dto.admin.request.RenewTenantRequest;
import com.shaxian.biz.entity.Subscription;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订阅 / 续费记录业务规则
 */
@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    /**
     * 校验续费请求合法性
     */
    public void validateRenew(Tenant tenant, RenewTenantRequest req) {
        if (req.getAmount() == null || req.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("金额必须 >= 0");
        }
        if (req.getNewExpiresAt() == null) {
            throw new IllegalArgumentException("延至日期不能为空");
        }
        LocalDateTime newExpiresAtTime = toEndOfDay(req.getNewExpiresAt());
        LocalDateTime currentExpiresAt = tenant.getExpiresAt() != null ? tenant.getExpiresAt() : LocalDateTime.now();
        if (!newExpiresAtTime.isAfter(currentExpiresAt)) {
            throw new IllegalArgumentException("延至日期必须晚于当前到期日");
        }
    }

    /**
     * 创建续费记录（必须先通过 validateRenew）
     */
    public Subscription createRecord(Tenant tenant, RenewTenantRequest req, Long operatorUserId) {
        Subscription s = new Subscription();
        s.setTenantId(tenant.getId());
        s.setAmount(req.getAmount());
        s.setPrevExpiresAt(tenant.getExpiresAt());
        s.setNewExpiresAt(toEndOfDay(req.getNewExpiresAt()));
        s.setOperatorUserId(operatorUserId);
        s.setNote(req.getNote());
        return subscriptionRepository.save(s);
    }

    public List<Subscription> listByTenant(Long tenantId) {
        return subscriptionRepository.findByTenantIdOrderByCreatedAtDesc(tenantId);
    }

    private LocalDateTime toEndOfDay(LocalDate date) {
        // 用当天 23:59:59，让"延至 2027-05-12"覆盖整个 2027-05-12
        return date.atTime(23, 59, 59);
    }
}
