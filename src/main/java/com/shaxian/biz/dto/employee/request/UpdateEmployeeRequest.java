package com.shaxian.biz.dto.employee.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "更新员工请求")
public class UpdateEmployeeRequest {
    @Size(max = 100, message = "员工姓名长度不能超过100")
    @Schema(description = "员工姓名", example = "张三")
    private String name;

    @Size(max = 100, message = "职位长度不能超过100")
    @Schema(description = "职位", example = "销售经理")
    private String position;

    @Size(max = 50, message = "联系电话长度不能超过50")
    @Schema(description = "联系电话", example = "13800138000")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Size(max = 100, message = "角色长度不能超过100")
    @Schema(description = "角色", example = "SALES")
    private String role;

    @Size(max = 255, message = "密码长度不能超过255")
    @Schema(description = "密码", example = "password123")
    private String password;

    @Schema(description = "员工状态", example = "active", allowableValues = {"active", "inactive"})
    private String status; // active, inactive
}
