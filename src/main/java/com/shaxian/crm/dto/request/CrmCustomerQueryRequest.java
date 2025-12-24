package com.shaxian.crm.dto.request;

import lombok.Data;

@Data
public class CrmCustomerQueryRequest {
    /**
     * 客户名称（模糊查询）
     */
    private String name;

    /**
     * 手机号（模糊查询）
     */
    private String phone;

    /**
     * 客户来源：ONLINE, OFFLINE, REFERRAL, EXHIBITION, ADVERTISING, OTHER
     */
    private String source;

    /**
     * 客户类型：OFFICIAL, POTENTIAL
     */
    private String type;
}

