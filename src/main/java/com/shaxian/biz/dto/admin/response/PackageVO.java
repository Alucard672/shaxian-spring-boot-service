package com.shaxian.biz.dto.admin.response;

import com.shaxian.biz.entity.TenantPackage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "套餐视图")
public class PackageVO {
    private Long id;
    private String name;
    private Integer concurrentLimit;
    private BigDecimal yearlyPrice;
    private TenantPackage.PackageStatus status;

    public static PackageVO from(TenantPackage p) {
        PackageVO v = new PackageVO();
        v.setId(p.getId());
        v.setName(p.getName());
        v.setConcurrentLimit(p.getConcurrentLimit());
        v.setYearlyPrice(p.getYearlyPrice());
        v.setStatus(p.getStatus());
        return v;
    }
}
