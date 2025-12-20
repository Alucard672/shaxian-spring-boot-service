package com.shaxian.service.tenant;

import com.shaxian.entity.Tenant;
import com.shaxian.repository.TenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public List<Tenant> getAll() {
        return tenantRepository.findAll();
    }

    public Optional<Tenant> getById(Long id) {
        return tenantRepository.findById(id);
    }

    @Transactional
    public Tenant create(Tenant tenant) {
        if (tenantRepository.existsByCode(tenant.getCode())) {
            throw new IllegalArgumentException("租户编码已存在");
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
