package com.shaxian.biz.service.tenant;

import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.util.TenantCodeGenerator;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;
    private final TenantCodeGenerator tenantCodeGenerator;

    @Value("${tenant.default-expiry-days:7}")
    private int defaultExpiryDays;

    public TenantService(TenantRepository tenantRepository, TenantCodeGenerator tenantCodeGenerator) {
        this.tenantRepository = tenantRepository;
        this.tenantCodeGenerator = tenantCodeGenerator;
    }

    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> getById(Long id) {
        return tenantRepository.findById(id);
    }

    public Page<Tenant> queryTenants(String name, String code, String status, Integer pageNo, Integer pageSize) {
        Specification<Tenant> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(code)) {
                predicates.add(cb.like(root.get("code"), "%" + code + "%"));
            }
            if (StringUtils.hasText(status)) {
                try {
                    Tenant.TenantStatus statusEnum = Tenant.TenantStatus.valueOf(status);
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的枚举值
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return tenantRepository.findAll(spec, pageable);
    }

    @Transactional
    public Tenant create(Tenant tenant) {
        // 自动生成租户代码
        if (tenant.getCode() == null || tenant.getCode().isEmpty()) {
            tenant.setCode(tenantCodeGenerator.generateCode());
        } else {
            if (tenantRepository.existsByCode(tenant.getCode())) {
                throw new IllegalArgumentException("租户编码已存在");
            }
        }

        // 设置默认有效期
        if (tenant.getExpiresAt() == null) {
            tenant.setExpiresAt(LocalDateTime.now().plusDays(defaultExpiryDays));
        }

        return tenantRepository.save(tenant);
    }

    @Transactional
    public Tenant update(Long id, Tenant tenant) {
        Tenant existing = tenantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));

        if (!existing.getCode().equals(tenant.getCode()) &&
                tenantRepository.existsByCode(tenant.getCode())) {
            throw new IllegalArgumentException("租户编码已存在");
        }

        tenant.setId(id);
        tenant.setCreatedAt(existing.getCreatedAt());
        return tenantRepository.save(tenant);
    }

    @Transactional
    public void delete(Long id) {
        if (!tenantRepository.existsById(id)) {
            throw new IllegalArgumentException("租户不存在");
        }
        tenantRepository.deleteById(id);
    }
}
