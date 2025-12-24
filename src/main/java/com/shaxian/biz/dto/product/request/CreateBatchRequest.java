package com.shaxian.biz.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "创建批次请求")
public class CreateBatchRequest {
    @NotNull(message = "色号ID不能为空")
    @Schema(description = "色号ID", required = true, example = "1")
    private Long colorId;

    @NotBlank(message = "缸号编码不能为空")
    @Size(max = 50, message = "缸号编码长度不能超过50")
    @Schema(description = "缸号编码", required = true, example = "BATCH001")
    private String code;

    @Schema(description = "生产日期", example = "2024-01-01")
    private LocalDate productionDate;

    @Schema(description = "供应商ID", example = "1")
    private Long supplierId;

    @Size(max = 200, message = "供应商名称长度不能超过200")
    @Schema(description = "供应商名称", example = "供应商A")
    private String supplierName;

    @Schema(description = "采购价格", example = "100.00")
    private BigDecimal purchasePrice;

    @NotNull(message = "初始数量不能为空")
    @Schema(description = "初始数量", required = true, example = "1000.00")
    private BigDecimal initialQuantity;

    @Size(max = 100, message = "库存位置长度不能超过100")
    @Schema(description = "库存位置", example = "A区-1号仓库")
    private String stockLocation;

    @Schema(description = "备注", example = "优质批次")
    private String remark;
}
