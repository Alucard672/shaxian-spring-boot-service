package com.shaxian.biz.dto.admin.response;

import com.shaxian.biz.auth.UserSession;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "活跃 session 视图（脱敏，不暴露 sessionId）")
public class ActiveSessionVO {
    private String phone;
    private String userName;
    private LocalDateTime createdAt;

    public static ActiveSessionVO from(UserSession s) {
        ActiveSessionVO v = new ActiveSessionVO();
        v.setPhone(s.getPhone());
        v.setUserName(s.getUsername());
        v.setCreatedAt(s.getCreatedAt());
        return v;
    }
}
