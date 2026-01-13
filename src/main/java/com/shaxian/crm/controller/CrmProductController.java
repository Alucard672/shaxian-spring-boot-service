package com.shaxian.crm.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.api.PageResult;
import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.appservice.CrmProductAppService;
import com.shaxian.crm.dto.request.CreateCrmProductRequest;
import com.shaxian.crm.dto.request.CrmProductQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmProductRequest;
import com.shaxian.crm.entity.CrmProduct;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/crm/api/products")
@Tag(name = "CRM商品管理", description = "软件产品管理接口")
public class CrmProductController {

    private final CrmProductAppService crmProductAppService;

    public CrmProductController(CrmProductAppService crmProductAppService) {
        this.crmProductAppService = crmProductAppService;
    }

    @PostMapping("/query")
    @Operation(summary = "查询CRM商品列表", description = "分页查询CRM商品，支持按名称、编码、状态等条件筛选")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品列表")
    })
    public ResponseEntity<ApiResponse<PageResult<CrmProduct>>> queryProducts(
            @Parameter(description = "页码，从1开始", required = true) @RequestParam Integer pageNo,
            @Parameter(description = "每页条数", required = true) @RequestParam Integer pageSize,
            @RequestBody(required = false) CrmProductQueryRequest request,
            CrmUserSession session) {
        if (request == null) {
            request = new CrmProductQueryRequest();
        }
        PageResult<CrmProduct> result = crmProductAppService.queryProducts(request, pageNo, pageSize);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在")
    })
    public ResponseEntity<ApiResponse<CrmProduct>> getProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            CrmUserSession session) {
        return crmProductAppService.findProduct(id)
                .map(product -> ResponseEntity.ok(ApiResponse.ok(product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("商品不存在")));
    }

    @PostMapping
    @Operation(summary = "新增商品", description = "创建新商品")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建商品")
    })
    public ResponseEntity<ApiResponse<CrmProduct>> createProduct(
            @Valid @RequestBody CreateCrmProductRequest request,
            CrmUserSession session) {
        CrmProduct created = crmProductAppService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "修改商品", description = "更新商品信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新商品")
    })
    public ResponseEntity<ApiResponse<CrmProduct>> updateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateCrmProductRequest request,
            CrmUserSession session) {
        CrmProduct updated = crmProductAppService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @PutMapping("/{id}/activate")
    @Operation(summary = "启用商品", description = "启用指定商品")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功启用商品")
    })
    public ResponseEntity<ApiResponse<CrmProduct>> activateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            CrmUserSession session) {
        CrmProduct product = crmProductAppService.activateProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }

    @PutMapping("/{id}/deactivate")
    @Operation(summary = "停用商品", description = "停用指定商品")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功停用商品")
    })
    public ResponseEntity<ApiResponse<CrmProduct>> deactivateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            CrmUserSession session) {
        CrmProduct product = crmProductAppService.deactivateProduct(id);
        return ResponseEntity.ok(ApiResponse.ok(product));
    }
}

