package com.shaxian.crm.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建CRM用户请求")
public class CreateCrmUserRequest {
    @NotBlank(message = "手机号不能为空")
    @Size(max = 50, message = "手机号长度不能超过50")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    @Schema(description = "手机号", required = true, example = "13800138000")
    private String phone;

    @NotBlank(message = "姓名不能为空")
    @Size(max = 200, message = "姓名长度不能超过200")
    @Schema(description = "姓名", required = true, example = "张三")
    private String name;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @Size(max = 200, message = "邮箱长度不能超过200")
    @Schema(description = "邮箱", required = true, example = "zhangsan@example.com")
    private String email;

    @Schema(description = "角色ID列表", required = true, example = "[1, 2]")
    private java.util.List<Long> roleIds;
}

