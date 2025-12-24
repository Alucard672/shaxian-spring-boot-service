package com.shaxian.biz.dto.inventory.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "库存盘点单查询请求")
public class InventoryCheckOrderQueryRequest {
    @Schema(description = "盘点状态", example = "COMPLETED", allowableValues = {"PLANNED", "CHECKING", "COMPLETED", "CANCELLED"})
    private String status;
    
    @Schema(description = "仓库", example = "A区-1号仓库")
    private String warehouse;
}
