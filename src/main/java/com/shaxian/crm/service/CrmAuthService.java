package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmUserInfo;
import com.shaxian.crm.repository.CrmUserInfoRepository;
import com.shaxian.crm.util.PasswordUtil;
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
     * 密码验证流程：前端已对密码进行SHA256加密，服务端再次SHA256加密后与数据库比对
     */
    public CrmUserInfo login(String phone, String password) {
        CrmUserInfo user = crmUserInfoRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 检查密码
        // 前端已经对密码进行了SHA256加密，这里再次进行SHA256加密
        String hashedPassword = PasswordUtil.hashPassword(password);
        
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码错误");
        }
        
        if (!user.getPassword().equals(hashedPassword)) {
            throw new IllegalArgumentException("密码错误");
        }

        // 检查用户状态
        if (user.getStatus() != CrmUserInfo.UserStatus.ACTIVE) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        return user;
    }
}

