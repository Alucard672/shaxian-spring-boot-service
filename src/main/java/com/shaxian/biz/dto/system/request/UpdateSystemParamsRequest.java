package com.shaxian.biz.dto.system.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新系统参数请求")
public class UpdateSystemParamsRequest {
    @Schema(description = "是否启用染色流程", example = "true")
    private Boolean enableDyeingProcess;
}
