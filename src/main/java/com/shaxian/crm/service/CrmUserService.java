package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmUserInfo;
import com.shaxian.crm.repository.CrmUserInfoRepository;
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

    public CrmUserService(CrmUserInfoRepository crmUserInfoRepository) {
        this.crmUserInfoRepository = crmUserInfoRepository;
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
     * 密码自动设置为手机号后六位
     */
    @Transactional
    public CrmUserInfo create(String phone, String name, String email) {
        // 检查手机号是否已存在
        if (crmUserInfoRepository.existsByPhone(phone)) {
            throw new IllegalArgumentException("手机号已存在");
        }

        CrmUserInfo user = new CrmUserInfo();
        user.setPhone(phone);
        user.setName(name);
        user.setEmail(email);
        
        // 密码设置为手机号后六位
        String password = phone.length() >= 6 ? phone.substring(phone.length() - 6) : phone;
        user.setPassword(password);
        
        // 默认状态为启用
        user.setStatus(CrmUserInfo.UserStatus.ACTIVE);
        
        return crmUserInfoRepository.save(user);
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public CrmUserInfo update(Long id, String phone, String name, String email) {
        CrmUserInfo existing = crmUserInfoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 如果手机号有变化，检查新手机号是否已存在
        if (StringUtils.hasText(phone) && !existing.getPhone().equals(phone) &&
                crmUserInfoRepository.existsByPhoneAndIdNot(phone, id)) {
            throw new IllegalArgumentException("手机号已存在");
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

