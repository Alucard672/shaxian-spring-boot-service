package com.shaxian.biz.dto.system.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新系统参数请求")
public class UpdateSystemParamsRequest {
    @Schema(description = "是否启用染色流程", example = "true")
    private Boolean enableDyeingProcess;

    @Schema(description = "是否允许负库存（销售出库时库存不足不提醒，直接扣减到负数）", example = "false")
    private Boolean allowNegativeStock;
}
