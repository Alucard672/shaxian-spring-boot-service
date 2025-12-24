package com.shaxian.biz.dto.template.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新打印模板请求")
public class UpdatePrintTemplateRequest {
    @Schema(description = "模板名称", example = "销售订单模板")
    private String name;

    @Schema(description = "模板类型", example = "A4_TEMPLATE", allowableValues = {"A4_TEMPLATE", "TRIPLE_FORM"})
    private String type; // A4_TEMPLATE, TRIPLE_FORM

    @Schema(description = "模板描述", example = "标准A4销售订单模板")
    private String description;

    @Schema(description = "是否默认模板", example = "false")
    private Boolean isDefault;

    @Schema(description = "文档类型", example = "SALES_ORDER", allowableValues = {"SALES_ORDER", "PURCHASE_ORDER"})
    private String documentType; // SALES_ORDER, PURCHASE_ORDER

    @Schema(description = "页面设置（JSON格式）", example = "{\"pageSize\":\"A4\",\"orientation\":\"portrait\"}")
    private String pageSettings; // JSON 格式

    @Schema(description = "标题设置（JSON格式）", example = "{\"title\":\"销售订单\",\"fontSize\":16}")
    private String titleSettings; // JSON 格式

    @Schema(description = "基本信息字段（JSON格式）", example = "{\"customerName\":true,\"orderDate\":true}")
    private String basicInfoFields; // JSON 格式

    @Schema(description = "商品字段（JSON格式）", example = "{\"productName\":true,\"quantity\":true}")
    private String productFields; // JSON 格式

    @Schema(description = "汇总字段（JSON格式）", example = "{\"totalAmount\":true,\"tax\":true}")
    private String summaryFields; // JSON 格式

    @Schema(description = "其他元素（JSON格式）", example = "{\"logo\":true,\"footer\":true}")
    private String otherElements; // JSON 格式
}
