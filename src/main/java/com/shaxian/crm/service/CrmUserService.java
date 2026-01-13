package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmRole;
import com.shaxian.crm.entity.CrmUserInfo;
import com.shaxian.crm.repository.CrmRoleRepository;
import com.shaxian.crm.repository.CrmUserInfoRepository;
import com.shaxian.crm.util.PasswordUtil;
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
public class CrmUserService {

    private final CrmUserInfoRepository crmUserInfoRepository;
    private final CrmRoleRepository crmRoleRepository;

    public CrmUserService(CrmUserInfoRepository crmUserInfoRepository, CrmRoleRepository crmRoleRepository) {
        this.crmUserInfoRepository = crmUserInfoRepository;
        this.crmRoleRepository = crmRoleRepository;
    }

    /**
     * 分页查询用户列表，支持多条件模糊查询
     */
    public Page<CrmUserInfo> queryUsers(String status, String phone, String name, String email, Integer pageNo, Integer pageSize) {
        Specification<CrmUserInfo> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(status)) {
                try {
                    CrmUserInfo.UserStatus statusEnum = CrmUserInfo.UserStatus.valueOf(status);
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的枚举值
                }
            }
            if (StringUtils.hasText(phone)) {
                predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
            }
            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(email)) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + email.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return crmUserInfoRepository.findAll(spec, pageable);
    }

    /**
     * 根据ID获取用户
     */
    public Optional<CrmUserInfo> getById(Long id) {
        return crmUserInfoRepository.findById(id);
    }

    /**
     * 创建用户
     * 密码自动设置为手机号后六位，并进行双重SHA256加密后存储
     */
    @Transactional
    public CrmUserInfo create(String phone, String name, String email, List<Long> roleIds) {
        // 检查手机号是否已存在
        if (crmUserInfoRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        // 校验角色ID列表
        validateRoleIds(roleIds);

        CrmUserInfo user = new CrmUserInfo();
        user.setPhone(phone);
        user.setName(name);
        user.setEmail(email);
        user.setRoleIdsList(roleIds);
        
        // 密码设置为手机号后六位，并进行双重SHA256加密
        String plainPassword = phone.length() >= 6 ? phone.substring(phone.length() - 6) : phone;
        String encryptedPassword = PasswordUtil.hashPasswordTwice(plainPassword);
        user.setPassword(encryptedPassword);
        
        // 默认状态为启用
        user.setStatus(CrmUserInfo.UserStatus.ACTIVE);
        
        return crmUserInfoRepository.save(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public CrmUserInfo update(Long id, String phone, String name, String email, List<Long> roleIds) {
        CrmUserInfo existing = crmUserInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 如果手机号有变化，检查新手机号是否已存在
        if (StringUtils.hasText(phone) && !existing.getPhone().equals(phone) &&
                crmUserInfoRepository.existsByPhoneAndIdNot(phone, id)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        // 如果提供了角色ID列表，进行校验并更新
        if (roleIds != null) {
            validateRoleIds(roleIds);
            existing.setRoleIdsList(roleIds);
        }

        // 只更新非空字段
        if (StringUtils.hasText(phone)) {
            existing.setPhone(phone);
        }
        if (StringUtils.hasText(name)) {
            existing.setName(name);
        }
        if (StringUtils.hasText(email)) {
            existing.setEmail(email);
        }

        return crmUserInfoRepository.save(existing);
    }

    /**
     * 校验角色ID列表
     * 1. 不能为空
     * 2. 至少包含一个角色
     * 3. 所有角色ID必须存在且为启用状态
     */
    private void validateRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("用户必须至少有一个角色");
        }

        // 检查所有角色是否存在且为启用状态
        List<CrmRole> roles = crmRoleRepository.findByIdInAndStatus(roleIds, CrmRole.RoleStatus.ACTIVE);
        if (roles.size() != roleIds.size()) {
            throw new IllegalArgumentException("部分角色不存在或已停用");
        }
    }

    /**
     * 更新用户状态
     */
    @Transactional
    public void updateStatus(Long id, CrmUserInfo.UserStatus status) {
        CrmUserInfo user = crmUserInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        user.setStatus(status);
        crmUserInfoRepository.save(user);
    }
}

