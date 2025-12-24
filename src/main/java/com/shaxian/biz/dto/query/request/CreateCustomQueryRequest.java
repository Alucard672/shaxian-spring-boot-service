package com.shaxian.biz.dto.query.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建自定义查询请求")
public class CreateCustomQueryRequest {
    @NotBlank(message = "查询名称不能为空")
    @Size(max = 200, message = "查询名称长度不能超过200")
    @Schema(description = "查询名称", required = true, example = "销售订单查询")
    private String name;

    @NotBlank(message = "模块不能为空")
    @Size(max = 50, message = "模块长度不能超过50")
    @Schema(description = "模块", required = true, example = "sales")
    private String module;

    @Schema(description = "查询条件（JSON格式）", example = "{\"status\":\"APPROVED\",\"startDate\":\"2024-01-01\"}")
    private String conditions; // JSON 格式存储查询条件
}
