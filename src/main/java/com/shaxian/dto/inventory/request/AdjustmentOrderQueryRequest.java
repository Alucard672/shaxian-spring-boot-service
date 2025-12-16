package com.shaxian.dto.inventory.request;

import lombok.Data;

@Data
public class AdjustmentOrderQueryRequest {
    private String status;
    private String type;
}
