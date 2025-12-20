package com.shaxian.service.tenant;

import com.shaxian.entity.Tenant;
import com.shaxian.repository.TenantRepository;
import com.shaxian.util.TenantCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
