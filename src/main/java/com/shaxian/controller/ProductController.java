package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.product.ProductAppService;
import com.shaxian.entity.Batch;
import com.shaxian.entity.Color;
import com.shaxian.entity.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductAppService productAppService;

    public ProductController(ProductAppService productAppService) {
        this.productAppService = productAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        List<Product> products = productAppService.listProducts();
        return ResponseEntity.ok(ApiResponse.ok(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProduct(@PathVariable Long id) {
        return productAppService.findProduct(id)
                .map(product -> ResponseEntity.ok(ApiResponse.ok(product)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("商品不存在")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        Product created = productAppService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productAppService.updateProduct(id, product);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productAppService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // ========== 色号管理 ==========
    @GetMapping("/{id}/colors")
    public ResponseEntity<ApiResponse<List<Color>>> getColors(@PathVariable Long id) {
        List<Color> colors = productAppService.listColors(id);
        return ResponseEntity.ok(ApiResponse.ok(colors));
    }

    @PostMapping("/{id}/colors")
    public ResponseEntity<ApiResponse<Color>> createColor(@PathVariable Long id, @RequestBody Color color) {
        Color created = productAppService.createColor(id, color);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/colors/{id}")
    public ResponseEntity<ApiResponse<Color>> updateColor(@PathVariable Long id, @RequestBody Color color) {
        Color updated = productAppService.updateColor(id, color);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/colors/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteColor(@PathVariable Long id) {
        productAppService.deleteColor(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // ========== 缸号管理 ==========
    @GetMapping("/colors/{colorId}/batches")
    public ResponseEntity<ApiResponse<List<Batch>>> getBatches(@PathVariable Long colorId) {
        List<Batch> batches = productAppService.listBatches(colorId);
        return ResponseEntity.ok(ApiResponse.ok(batches));
    }

    @PostMapping("/colors/{colorId}/batches")
    public ResponseEntity<ApiResponse<Batch>> createBatch(@PathVariable Long colorId, @RequestBody Batch batch) {
        Batch created = productAppService.createBatch(colorId, batch);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/batches/{id}")
    public ResponseEntity<ApiResponse<Batch>> updateBatch(@PathVariable Long id, @RequestBody Batch batch) {
        Batch updated = productAppService.updateBatch(id, batch);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/batches/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBatch(@PathVariable Long id) {
        productAppService.deleteBatch(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

