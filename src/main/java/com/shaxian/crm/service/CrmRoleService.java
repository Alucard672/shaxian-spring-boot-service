package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmRole;
import com.shaxian.crm.repository.CrmRoleRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CrmRoleService {

    private final CrmRoleRepository crmRoleRepository;

    public CrmRoleService(CrmRoleRepository crmRoleRepository) {
        this.crmRoleRepository = crmRoleRepository;
    }

    /**
     * 分页查询角色列表，支持多条件模糊查询
     */
    public Page<CrmRole> queryRoles(String name, String code, String status, Integer pageNo, Integer pageSize) {
        Specification<CrmRole> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(code)) {
                predicates.add(cb.like(root.get("code"), "%" + code + "%"));
            }
            if (StringUtils.hasText(status)) {
                try {
                    CrmRole.RoleStatus statusEnum = CrmRole.RoleStatus.valueOf(status);
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的枚举值
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return crmRoleRepository.findAll(spec, pageable);
    }

    /**
     * 根据ID获取角色
     */
    public Optional<CrmRole> getById(Long id) {
        return crmRoleRepository.findById(id);
    }

    /**
     * 创建角色
     */
    @Transactional
    public CrmRole create(CrmRole role) {
        // 检查角色代码是否已存在
        if (crmRoleRepository.existsByCode(role.getCode())) {
            throw new IllegalArgumentException("角色代码已存在");
        }

        // 如果级别为空，设置为0
        if (role.getLevel() == null) {
            role.setLevel(0);
        }

        // 如果状态为空，设置为启用
        if (role.getStatus() == null) {
            role.setStatus(CrmRole.RoleStatus.ACTIVE);
        }

        return crmRoleRepository.save(role);
    }

    /**
     * 更新角色
     */
    @Transactional
    public CrmRole update(Long id, CrmRole role) {
        CrmRole existing = crmRoleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));

        // 如果角色代码有变化，检查新代码是否已存在
        if (StringUtils.hasText(role.getCode()) && !existing.getCode().equals(role.getCode()) &&
                crmRoleRepository.existsByCodeAndIdNot(role.getCode(), id)) {
            throw new IllegalArgumentException("角色代码已存在");
        }

        // 只更新非空字段
        if (StringUtils.hasText(role.getName())) {
            existing.setName(role.getName());
        }
        if (StringUtils.hasText(role.getCode())) {
            existing.setCode(role.getCode());
        }
        if (role.getLevel() != null) {
            existing.setLevel(role.getLevel());
        }
        if (role.getStatus() != null) {
            existing.setStatus(role.getStatus());
        }
        if (role.getDescription() != null) {
            existing.setDescription(role.getDescription());
        }

        return crmRoleRepository.save(existing);
    }

    /**
     * 更新角色状态
     */
    @Transactional
    public void updateStatus(Long id, CrmRole.RoleStatus status) {
        CrmRole role = crmRoleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("角色不存在"));
        role.setStatus(status);
        crmRoleRepository.save(role);
    }

    /**
     * 根据ID列表批量获取角色
     */
    public List<CrmRole> getRolesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return crmRoleRepository.findByIdIn(ids);
    }

    /**
     * 根据ID列表批量获取启用状态的角色
     */
    public List<CrmRole> getActiveRolesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        return crmRoleRepository.findByIdInAndStatus(ids, CrmRole.RoleStatus.ACTIVE);
    }
}
