package com.shaxian.crm.auth;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * CRM用户会话信息
 */
@Data
public class CrmUserSession {
    /**
     * 会话唯一标识
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 角色ID列表
     */
    private List<Long> roleIds = new ArrayList<>();

    /**
     * 角色名称列表
     */
    private List<String> roleNames = new ArrayList<>();

    /**
     * 角色代码列表
     */
    private List<String> roleCodes = new ArrayList<>();

    public CrmUserSession() {
    }

    public CrmUserSession(String sessionId, Long userId, String username, String phone, String email) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.username = username;
        this.phone = phone;
        this.email = email;
    }
}
