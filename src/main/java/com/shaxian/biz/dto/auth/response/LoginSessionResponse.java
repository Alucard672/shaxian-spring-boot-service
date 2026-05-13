package com.shaxian.biz.dto.auth.response;

import com.shaxian.biz.auth.UserSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 登录响应 / 当前会话信息 VO
 * 在 UserSession 字段基础上额外暴露 remainingDays（剩余天数）
 */
@Schema(description = "登录会话信息")
@Data
public class LoginSessionResponse {

    private String sessionId;
    private Long userId;
    private String username;
    private String phone;
    private String email;
    private String role;
    private String position;
    private Long tenantId;
    private String tenantName;
    private String tenantCode;
    private boolean superAdmin;
    private LocalDateTime tenantExpiresAt;
    /**
     * 距离到期剩余天数；超管或租户无到期时间时为 null
     */
    private Long remainingDays;

    public static LoginSessionResponse from(UserSession session) {
        LoginSessionResponse r = new LoginSessionResponse();
        r.setSessionId(session.getSessionId());
        r.setUserId(session.getUserId());
        r.setUsername(session.getUsername());
        r.setPhone(session.getPhone());
        r.setEmail(session.getEmail());
        r.setRole(session.getRole());
        r.setPosition(session.getPosition());
        r.setTenantId(session.getTenantId());
        r.setTenantName(session.getTenantName());
        r.setTenantCode(session.getTenantCode());
        r.setSuperAdmin(session.isSuperAdmin());
        r.setTenantExpiresAt(session.getTenantExpiresAt());
        if (session.getTenantExpiresAt() != null) {
            r.setRemainingDays(ChronoUnit.DAYS.between(LocalDateTime.now(), session.getTenantExpiresAt()));
        }
        return r;
    }
}
