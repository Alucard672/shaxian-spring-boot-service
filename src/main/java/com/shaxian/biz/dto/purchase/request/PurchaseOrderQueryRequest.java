package com.shaxian.biz.dto.purchase.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PurchaseOrderQueryRequest {
    private String status;
    private Long supplierId;
    private LocalDate startDate;
    private LocalDate endDate;
}
