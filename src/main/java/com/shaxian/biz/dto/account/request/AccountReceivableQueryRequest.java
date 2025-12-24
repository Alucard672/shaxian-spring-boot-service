package com.shaxian.biz.dto.account.request;

import lombok.Data;

@Data
public class AccountReceivableQueryRequest {
    private String customerId;
    private String status;
}
