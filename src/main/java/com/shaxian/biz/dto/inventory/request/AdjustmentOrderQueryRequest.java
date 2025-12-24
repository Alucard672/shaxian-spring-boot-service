package com.shaxian.biz.dto.inventory.request;

import lombok.Data;

@Data
public class AdjustmentOrderQueryRequest {
    private String status;
    private String type;
}
