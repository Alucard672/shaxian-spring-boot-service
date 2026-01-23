package com.shaxian.biz.controller;

import com.shaxian.biz.api.ApiResponse;
import com.shaxian.biz.appservice.product.ProductAppService;
import com.shaxian.biz.auth.UserSession;
import com.shaxian.biz.dto.product.request.CreateBatchRequest;
import com.shaxian.biz.dto.product.request.CreateColorRequest;
import com.shaxian.biz.dto.product.request.CreateProductRequest;
import com.shaxian.biz.dto.product.request.UpdateBatchRequest;
import com.shaxian.biz.dto.product.request.UpdateColorRequest;
import com.shaxian.biz.dto.product.request.UpdateProductRequest;
import com.shaxian.biz.dto.product.response.ShareCodeResponse;
import com.shaxian.biz.entity.Batch;
import com.shaxian.biz.entity.Color;
import com.shaxian.biz.entity.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/biz/api/products")
@Tag(name = "商品管理", description = "商品、色号、缸号管理接口")
public class ProductController {

    private final ProductAppService productAppService;

    public ProductController(ProductAppService productAppService) {
        this.productAppService = productAppService;
    }

    @GetMapping
    @Operation(summary = "获取所有商品", description = "获取商品列表")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品列表")
    })
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts(UserSession session) {
        List<Product> products = productAppService.listProducts();
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取商品详情", description = "根据ID获取商品信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在")
    })
    public ResponseEntity<ApiResponse<Product>> getProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            UserSession session) {
        return productAppService.findProduct(id)
                .map(product -> ResponseEntity.ok(ApiResponse.ok(product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("商品不存在")));
    }

    @PostMapping
    @Operation(summary = "创建商品", description = "创建新商品")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建商品")
    })
    public ResponseEntity<ApiResponse<Product>> createProduct(
            @RequestBody CreateProductRequest request,
            UserSession session) {
        Product created = productAppService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新商品", description = "更新商品信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新商品")
    })
    public ResponseEntity<ApiResponse<Product>> updateProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @RequestBody UpdateProductRequest request,
            UserSession session) {
        Product updated = productAppService.updateProduct(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除商品", description = "删除指定商品")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除商品")
    })
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            UserSession session) {
        productAppService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // ========== 色号管理 ==========
    @GetMapping("/{id}/colors")
    @Operation(summary = "获取商品色号列表", description = "获取指定商品的所有色号")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取色号列表")
    })
    public ResponseEntity<ApiResponse<List<Color>>> getColors(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            UserSession session) {
        List<Color> colors = productAppService.listColors(id);
        return ResponseEntity.ok(ApiResponse.ok(colors));
    }

    @PostMapping("/{id}/colors")
    @Operation(summary = "创建色号", description = "为指定商品创建新色号")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建色号")
    })
    public ResponseEntity<ApiResponse<Color>> createColor(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            @RequestBody CreateColorRequest request,
            UserSession session) {
        Color created = productAppService.createColor(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/colors/{id}")
    @Operation(summary = "更新色号", description = "更新色号信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新色号")
    })
    public ResponseEntity<ApiResponse<Color>> updateColor(
            @Parameter(description = "色号ID", required = true) @PathVariable Long id,
            @RequestBody UpdateColorRequest request,
            UserSession session) {
        Color updated = productAppService.updateColor(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/colors/{id}")
    @Operation(summary = "删除色号", description = "删除指定色号")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除色号")
    })
    public ResponseEntity<ApiResponse<Void>> deleteColor(
            @Parameter(description = "色号ID", required = true) @PathVariable Long id,
            UserSession session) {
        productAppService.deleteColor(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // ========== 缸号管理 ==========
    @GetMapping("/colors/{colorId}/batches")
    @Operation(summary = "获取色号缸号列表", description = "获取指定色号的所有缸号")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取缸号列表")
    })
    public ResponseEntity<ApiResponse<List<Batch>>> getBatches(
            @Parameter(description = "色号ID", required = true) @PathVariable Long colorId,
            UserSession session) {
        List<Batch> batches = productAppService.listBatches(colorId);
        return ResponseEntity.ok(ApiResponse.ok(batches));
    }

    @PostMapping("/colors/{colorId}/batches")
    @Operation(summary = "创建缸号", description = "为指定色号创建新缸号")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "成功创建缸号")
    })
    public ResponseEntity<ApiResponse<Batch>> createBatch(
            @Parameter(description = "色号ID", required = true) @PathVariable Long colorId,
            @RequestBody CreateBatchRequest request,
            UserSession session) {
        Batch created = productAppService.createBatch(colorId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/batches/{id}")
    @Operation(summary = "更新缸号", description = "更新缸号信息")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功更新缸号")
    })
    public ResponseEntity<ApiResponse<Batch>> updateBatch(
            @Parameter(description = "缸号ID", required = true) @PathVariable Long id,
            @RequestBody UpdateBatchRequest request,
            UserSession session) {
        Batch updated = productAppService.updateBatch(id, request);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/batches/{id}")
    @Operation(summary = "删除缸号", description = "删除指定缸号")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "成功删除缸号")
    })
    public ResponseEntity<ApiResponse<Void>> deleteBatch(
            @Parameter(description = "缸号ID", required = true) @PathVariable Long id,
            UserSession session) {
        productAppService.deleteBatch(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // ========== 分享码管理 ==========
    @PostMapping("/{id}/share-code")
    @Operation(summary = "生成商品分享码", description = "为指定商品生成分享码，需要登录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功生成分享码"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在")
    })
    public ResponseEntity<ApiResponse<ShareCodeResponse>> generateShareCode(
            @Parameter(description = "商品ID", required = true) @PathVariable Long id,
            UserSession session) {
        ShareCodeResponse shareCode = productAppService.generateShareCode(id, session.getTenantId());
        return ResponseEntity.ok(ApiResponse.ok(shareCode));
    }

    @GetMapping("/share/{code}")
    @Operation(summary = "根据分享码获取商品详情", description = "通过分享码获取商品详情，无需登录")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "成功获取商品信息"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "分享码格式错误或验证失败"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "商品不存在或租户不存在")
    })
    public ResponseEntity<ApiResponse<Product>> getProductByShareCode(
            @Parameter(description = "分享码", required = true) @PathVariable String code) {
        return productAppService.getProductByShareCode(code)
                .map(product -> ResponseEntity.ok(ApiResponse.ok(product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("商品不存在")));
    }
}

