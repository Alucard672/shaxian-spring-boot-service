package com.shaxian.biz.dto.admin.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "续费请求")
public class RenewTenantRequest {
    @NotNull(message = "金额不能为空")
    @PositiveOrZero(message = "金额必须 >= 0")
    @Schema(description = "续费金额，0 视为赠送", required = true, example = "2000.00")
    private BigDecimal amount;

    @NotNull(message = "延至日期不能为空")
    @Schema(description = "续费后的到期日（必须晚于当前 expires_at）", required = true, example = "2027-05-12")
    private LocalDate newExpiresAt;

    @Schema(description = "备注", example = "年费续费")
    private String note;
}
