package com.shaxian.biz.auth;

import lombok.Data;

/**
 * 用户会话信息
 */
@Data
public class UserSession {
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
     * 角色
     */
    private String role;

    /**
     * 职位
     */
    private String position;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 租户代码
     */
    private String tenantCode;

    public UserSession() {
    }

    public UserSession(String sessionId, Long userId, String username, String phone, String email, String role, String position) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.position = position;
    }

    public UserSession(String sessionId, Long userId, String username, String phone, String email, String role, String position, Long tenantId, String tenantName, String tenantCode) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.username = username;
        this.phone = phone;
        this.email = email;
        this.role = role;
        this.position = position;
        this.tenantId = tenantId;
        this.tenantName = tenantName;
        this.tenantCode = tenantCode;
    }
}
