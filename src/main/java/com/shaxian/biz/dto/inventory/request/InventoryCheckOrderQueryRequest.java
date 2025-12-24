package com.shaxian.biz.dto.inventory.request;

import lombok.Data;

@Data
public class InventoryCheckOrderQueryRequest {
    private String status;
    private String warehouse;
}
