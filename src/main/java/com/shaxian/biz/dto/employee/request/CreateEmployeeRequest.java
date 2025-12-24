package com.shaxian.biz.dto.employee.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateEmployeeRequest {
    @NotBlank(message = "员工姓名不能为空")
    @Size(max = 100, message = "员工姓名长度不能超过100")
    private String name;

    @Size(max = 100, message = "职位长度不能超过100")
    private String position;

    @Size(max = 50, message = "联系电话长度不能超过50")
    private String phone;

    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    @Size(max = 100, message = "角色长度不能超过100")
    private String role;

    @Size(max = 255, message = "密码长度不能超过255")
    private String password;

    private String status; // active, inactive
}
