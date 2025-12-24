package com.shaxian.biz.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "登录请求")
public class LoginRequest {
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", required = true, example = "password123")
    private String password;
}
