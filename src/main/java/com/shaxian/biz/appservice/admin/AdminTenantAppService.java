package com.shaxian.biz.appservice.admin;

import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.auth.BizUserSessionManager;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.admin.request.RenewTenantRequest;
import com.shaxian.biz.dto.admin.request.TenantAdminQueryRequest;
import com.shaxian.biz.dto.admin.response.ActiveSessionVO;
import com.shaxian.biz.dto.admin.response.SubscriptionVO;
import com.shaxian.biz.dto.admin.response.TenantDetailVO;
import com.shaxian.biz.dto.admin.response.TenantVO;
import com.shaxian.biz.dto.tenant.request.CreateTenantRequest;
import com.shaxian.biz.dto.tenant.request.UpdateTenantRequest;
import com.shaxian.biz.entity.Subscription;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.TenantPackage;
import com.shaxian.biz.entity.User;
import com.shaxian.biz.repository.SubscriptionRepository;
import com.shaxian.biz.repository.TenantPackageRepository;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.repository.UserRepository;
import com.shaxian.biz.service.admin.SubscriptionService;
import com.shaxian.biz.service.tenant.TenantService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理端租户管理 AppService
 */
@Service
public class AdminTenantAppService {

    private final TenantService tenantService;
    private final TenantRepository tenantRepository;
    private final TenantPackageRepository tenantPackageRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;
    private final BizUserSessionManager sessionManager;

    public AdminTenantAppService(TenantService tenantService,
                                 TenantRepository tenantRepository,
                                 TenantPackageRepository tenantPackageRepository,
                                 SubscriptionRepository subscriptionRepository,
                                 SubscriptionService subscriptionService,
                                 UserRepository userRepository,
                                 BizUserSessionManager sessionManager) {
        this.tenantService = tenantService;
        this.tenantRepository = tenantRepository;
        this.tenantPackageRepository = tenantPackageRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
        this.sessionManager = sessionManager;
    }

    // ==================== list ====================

    public PageResult<TenantVO> listTenants(TenantAdminQueryRequest req, Integer pageNo, Integer pageSize) {
        Page<Tenant> page = tenantService.queryTenants(req.getName(), req.getCode(), req.getStatus(), pageNo, pageSize);
        List<Tenant> tenants = page.getContent();

        // 内存级 expiringDays 过滤
        if (req.getExpiringDays() != null && req.getExpiringDays() > 0) {
            LocalDateTime cutoff = LocalDateTime.now().plusDays(req.getExpiringDays());
            tenants = tenants.stream()
                    .filter(t -> t.getExpiresAt() != null
                            && t.getExpiresAt().isBefore(cutoff))
                    .collect(Collectors.toList());
        }

        // 套餐名 / 业务员名 批量 enrich
        Map<Long, String> packageNameMap = packageNameMap(tenants);
        Map<Long, Integer> packageLimitMap = packageLimitMap(tenants);
        Map<Long, String> assignedUserNameMap = userNameMap(tenants);

        List<TenantVO> vos = tenants.stream().map(t -> {
            TenantVO v = TenantVO.from(t);
            v.setPackageName(packageNameMap.get(t.getPackageId()));
            v.setPackageConcurrentLimit(packageLimitMap.get(t.getPackageId()));
            v.setAssignedUserName(assignedUserNameMap.get(t.getAssignedUserId()));
            return v;
        }).collect(Collectors.toList());

        return PageResult.of(vos, page.getTotalElements(), pageNo, pageSize);
    }

    // ==================== create ====================

    public TenantVO createTenant(CreateTenantRequest request, Long operatorUserId) {
        // 校验 expiresAt
        if (request.getExpiresAt() == null) {
            throw new IllegalArgumentException("初始有效期不能为空");
        }

        Tenant tenant = new Tenant();
        tenant.setName(request.getName());
        tenant.setAddress(request.getAddress());
        tenant.setExpiresAt(request.getExpiresAt());
        tenant.setAssignedUserId(request.getAssignedUserId());

        // 套餐：缺省取"标准版"
        Long packageId = request.getPackageId();
        if (packageId == null) {
            packageId = tenantPackageRepository.findByName("标准版")
                    .map(TenantPackage::getId)
                    .orElse(null);
        } else {
            tenantPackageRepository.findById(packageId)
                    .orElseThrow(() -> new IllegalArgumentException("套餐不存在"));
        }
        tenant.setPackageId(packageId);

        Tenant saved = tenantService.create(tenant);
        TenantVO vo = TenantVO.from(saved);
        enrichSingle(vo, saved);
        return vo;
    }

    // ==================== detail ====================

    public TenantDetailVO getTenantDetail(Long id) {
        Tenant t = tenantService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));

        TenantDetailVO vo = new TenantDetailVO();
        // 复制 TenantVO 字段
        TenantVO base = TenantVO.from(t);
        vo.setId(base.getId());
        vo.setName(base.getName());
        vo.setCode(base.getCode());
        vo.setAddress(base.getAddress());
        vo.setExpiresAt(base.getExpiresAt());
        vo.setStatus(base.getStatus());
        vo.setPackageId(base.getPackageId());
        vo.setAssignedUserId(base.getAssignedUserId());
        vo.setRemainingDays(base.getRemainingDays());
        vo.setCreatedAt(base.getCreatedAt());
        vo.setUpdatedAt(base.getUpdatedAt());
        enrichSingle(vo, t);

        // 订阅记录
        List<Subscription> subs = subscriptionService.listByTenant(id);
        Map<Long, String> operatorNameMap = subs.stream()
                .map(Subscription::getOperatorUserId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toMap(uid -> uid,
                        uid -> userRepository.findById(uid).map(User::getName).orElse("(未知)"),
                        (a, b) -> a));
        vo.setSubscriptions(subs.stream()
                .map(s -> SubscriptionVO.from(s, operatorNameMap.get(s.getOperatorUserId())))
                .collect(Collectors.toList()));

        // 活跃 session
        List<UserSession> sessions = sessionManager.listActiveSessionsByTenant(id);
        vo.setActiveSessions(sessions.stream().map(ActiveSessionVO::from).collect(Collectors.toList()));

        return vo;
    }

    // ==================== update ====================

    /**
     * 更新租户。若 status 或 expiresAt 字段被修改，事务提交后主动 evict 该租户所有 session。
     */
    public TenantVO updateTenant(Long id, UpdateTenantRequest request) {
        Tenant before = tenantService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        Tenant.TenantStatus beforeStatus = before.getStatus();
        LocalDateTime beforeExpiresAt = before.getExpiresAt();

        Tenant updated = doUpdate(id, request);

        boolean statusChanged = updated.getStatus() != beforeStatus;
        boolean expiresChanged = !Objects.equals(updated.getExpiresAt(), beforeExpiresAt);
        if (statusChanged || expiresChanged) {
            sessionManager.evictByTenantId(id);
        }

        TenantVO vo = TenantVO.from(updated);
        enrichSingle(vo, updated);
        return vo;
    }

    @Transactional
    protected Tenant doUpdate(Long id, UpdateTenantRequest request) {
        Tenant t = tenantService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        if (request.getName() != null) t.setName(request.getName());
        if (request.getAddress() != null) t.setAddress(request.getAddress());
        if (request.getStatus() != null) {
            try {
                t.setStatus(Tenant.TenantStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的状态值: " + request.getStatus());
            }
        }
        if (request.getExpiresAt() != null) t.setExpiresAt(request.getExpiresAt());
        if (request.getPackageId() != null) {
            tenantPackageRepository.findById(request.getPackageId())
                    .orElseThrow(() -> new IllegalArgumentException("套餐不存在"));
            t.setPackageId(request.getPackageId());
        }
        if (request.getAssignedUserId() != null) t.setAssignedUserId(request.getAssignedUserId());
        return tenantService.update(id, t);
    }

    // ==================== renew ====================

    /**
     * 续费：写 subscription + 更新 expiresAt + evict
     */
    public SubscriptionVO renew(Long id, RenewTenantRequest request, Long operatorUserId) {
        SubscriptionAndOperator result = doRenew(id, request, operatorUserId);
        // 事务后 evict
        sessionManager.evictByTenantId(id);
        return SubscriptionVO.from(result.subscription, result.operatorName);
    }

    @Transactional
    protected SubscriptionAndOperator doRenew(Long id, RenewTenantRequest request, Long operatorUserId) {
        Tenant tenant = tenantService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));
        subscriptionService.validateRenew(tenant, request);

        Subscription sub = subscriptionService.createRecord(tenant, request, operatorUserId);
        // 更新 tenant.expires_at
        tenant.setExpiresAt(sub.getNewExpiresAt());
        tenantRepository.save(tenant);

        String operatorName = userRepository.findById(operatorUserId).map(User::getName).orElse(null);
        return new SubscriptionAndOperator(sub, operatorName);
    }

    private static class SubscriptionAndOperator {
        final Subscription subscription;
        final String operatorName;

        SubscriptionAndOperator(Subscription subscription, String operatorName) {
            this.subscription = subscription;
            this.operatorName = operatorName;
        }
    }

    // ==================== enrich helpers ====================

    private void enrichSingle(TenantVO vo, Tenant t) {
        if (t.getPackageId() != null) {
            tenantPackageRepository.findById(t.getPackageId()).ifPresent(p -> {
                vo.setPackageName(p.getName());
                vo.setPackageConcurrentLimit(p.getConcurrentLimit());
            });
        }
        if (t.getAssignedUserId() != null) {
            userRepository.findById(t.getAssignedUserId()).ifPresent(u -> vo.setAssignedUserName(u.getName()));
        }
    }

    private Map<Long, String> packageNameMap(List<Tenant> tenants) {
        Set<Long> ids = tenants.stream().map(Tenant::getPackageId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) return new HashMap<>();
        return tenantPackageRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(TenantPackage::getId, TenantPackage::getName));
    }

    private Map<Long, Integer> packageLimitMap(List<Tenant> tenants) {
        Set<Long> ids = tenants.stream().map(Tenant::getPackageId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) return new HashMap<>();
        return tenantPackageRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(TenantPackage::getId, TenantPackage::getConcurrentLimit));
    }

    private Map<Long, String> userNameMap(List<Tenant> tenants) {
        Set<Long> ids = tenants.stream().map(Tenant::getAssignedUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        if (ids.isEmpty()) return new HashMap<>();
        return userRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(User::getId, User::getName));
    }
}
