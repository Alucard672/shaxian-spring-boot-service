package com.shaxian.dto.account.request;

import lombok.Data;

@Data
public class AccountPayableQueryRequest {
    private String supplierId;
    private String status;
}
