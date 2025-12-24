package com.shaxian.biz.dto.purchase.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PurchaseOrderItemRequest {
    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @Size(max = 200, message = "商品名称长度不能超过200")
    private String productName;

    @Size(max = 50, message = "商品编码长度不能超过50")
    private String productCode;

    private Long colorId;

    @Size(max = 100, message = "色号名称长度不能超过100")
    private String colorName;

    @Size(max = 50, message = "色号编码长度不能超过50")
    private String colorCode;

    @NotBlank(message = "缸号编码不能为空")
    @Size(max = 50, message = "缸号编码长度不能超过50")
    private String batchCode;

    @NotNull(message = "数量不能为空")
    private BigDecimal quantity;

    @Size(max = 20, message = "单位长度不能超过20")
    private String unit;

    @NotNull(message = "单价不能为空")
    private BigDecimal price;

    private LocalDate productionDate;

    @Size(max = 100, message = "库存位置长度不能超过100")
    private String stockLocation;

    private String remark;
}
