package com.shaxian.dto.template.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatePrintTemplateRequest {
    @NotBlank(message = "模板名称不能为空")
    private String name;

    private String type; // A4_TEMPLATE, TRIPLE_FORM

    private String description;

    private Boolean isDefault;

    private String documentType; // SALES_ORDER, PURCHASE_ORDER

    private String pageSettings; // JSON 格式

    private String titleSettings; // JSON 格式

    private String basicInfoFields; // JSON 格式

    private String productFields; // JSON 格式

    private String summaryFields; // JSON 格式

    private String otherElements; // JSON 格式
}
