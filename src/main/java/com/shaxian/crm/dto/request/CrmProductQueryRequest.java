package com.shaxian.crm.dto.request;

import lombok.Data;

@Data
public class CrmProductQueryRequest {
    /**
     * 商品名称（模糊查询）
     */
    private String name;

    /**
     * 商品编码（模糊查询）
     */
    private String code;

    /**
     * 商品状态：ACTIVE, INACTIVE
     */
    private String status;
}

