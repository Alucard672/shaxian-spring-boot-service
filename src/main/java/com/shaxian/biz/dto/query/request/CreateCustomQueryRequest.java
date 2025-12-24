package com.shaxian.biz.dto.query.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCustomQueryRequest {
    @NotBlank(message = "查询名称不能为空")
    @Size(max = 200, message = "查询名称长度不能超过200")
    private String name;

    @NotBlank(message = "模块不能为空")
    @Size(max = 50, message = "模块长度不能超过50")
    private String module;

    private String conditions; // JSON 格式存储查询条件
}
