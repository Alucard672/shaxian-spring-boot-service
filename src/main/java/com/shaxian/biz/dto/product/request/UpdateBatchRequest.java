package com.shaxian.biz.dto.product.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateBatchRequest {
    @Size(max = 50, message = "缸号编码长度不能超过50")
    private String code;

    private LocalDate productionDate;

    private Long supplierId;

    @Size(max = 200, message = "供应商名称长度不能超过200")
    private String supplierName;

    private BigDecimal purchasePrice;

    private BigDecimal initialQuantity;

    @Size(max = 100, message = "库存位置长度不能超过100")
    private String stockLocation;

    private String remark;
}
