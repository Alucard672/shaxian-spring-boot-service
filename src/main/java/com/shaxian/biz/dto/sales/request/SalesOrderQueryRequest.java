package com.shaxian.biz.dto.sales.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SalesOrderQueryRequest {
    private String status;
    private Long customerId;
    private LocalDate startDate;
    private LocalDate endDate;
}
