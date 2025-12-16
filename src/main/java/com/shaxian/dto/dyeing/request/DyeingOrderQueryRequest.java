package com.shaxian.dto.dyeing.request;

import lombok.Data;

@Data
public class DyeingOrderQueryRequest {
    private String status;
    private Long productId;
}
