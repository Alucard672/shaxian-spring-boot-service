package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmUserInfo;
import com.shaxian.crm.repository.CrmUserInfoRepository;
import org.springframework.stereotype.Service;

@Service
public class CrmAuthService {

    private final CrmUserInfoRepository crmUserInfoRepository;

    public CrmAuthService(CrmUserInfoRepository crmUserInfoRepository) {
        this.crmUserInfoRepository = crmUserInfoRepository;
    }

    /**
     * CRM用户登录验证
     * 验证手机号和密码，返回通过校验的用户信息
     */
    public CrmUserInfo login(String phone, String password) {
        CrmUserInfo user = crmUserInfoRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 检查密码（默认密码123456）
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            if (!"123456".equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        } else {
            if (!user.getPassword().equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        }

        // 检查用户状态
        if (user.getStatus() != CrmUserInfo.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        return user;
    }
}

