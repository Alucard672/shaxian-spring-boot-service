package com.shaxian.biz.appservice.admin;

import com.shaxian.biz.dto.admin.response.DashboardVO;
import com.shaxian.biz.dto.admin.response.TenantVO;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.TenantPackage;
import com.shaxian.biz.entity.User;
import com.shaxian.biz.repository.TenantPackageRepository;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminDashboardAppService {

    private static final int EXPIRING_WINDOW_DAYS = 30;

    private final TenantRepository tenantRepository;
    private final TenantPackageRepository packageRepository;
    private final UserRepository userRepository;

    public AdminDashboardAppService(TenantRepository tenantRepository,
                                    TenantPackageRepository packageRepository,
                                    UserRepository userRepository) {
        this.tenantRepository = tenantRepository;
        this.packageRepository = packageRepository;
        this.userRepository = userRepository;
    }

    public DashboardVO getDashboard() {
        LocalDateTime now = LocalDateTime.now();
        DashboardVO d = new DashboardVO();
        d.setTotalTenants(tenantRepository.count());
        d.setActiveTenants(tenantRepository.countByStatus(Tenant.TenantStatus.ACTIVE));

        List<Tenant> expiring = tenantRepository.findByExpiresAtBetween(now, now.plusDays(EXPIRING_WINDOW_DAYS));
        List<Tenant> expired = tenantRepository.findByExpiresAtBefore(now);

        d.setExpiringIn30Days(toVOs(expiring));
        d.setExpired(toVOs(expired));
        return d;
    }

    private List<TenantVO> toVOs(List<Tenant> tenants) {
        if (tenants.isEmpty()) return List.of();

        Set<Long> pkgIds = tenants.stream().map(Tenant::getPackageId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, TenantPackage> pkgMap = pkgIds.isEmpty() ? new HashMap<>()
                : packageRepository.findAllById(pkgIds).stream().collect(Collectors.toMap(TenantPackage::getId, p -> p));

        Set<Long> userIds = tenants.stream().map(Tenant::getAssignedUserId).filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Long, User> userMap = userIds.isEmpty() ? new HashMap<>()
                : userRepository.findAllById(userIds).stream().collect(Collectors.toMap(User::getId, u -> u));

        return tenants.stream().map(t -> {
            TenantVO v = TenantVO.from(t);
            TenantPackage p = pkgMap.get(t.getPackageId());
            if (p != null) {
                v.setPackageName(p.getName());
                v.setPackageConcurrentLimit(p.getConcurrentLimit());
            }
            User u = userMap.get(t.getAssignedUserId());
            if (u != null) v.setAssignedUserName(u.getName());
            return v;
        }).collect(Collectors.toList());
    }
}
