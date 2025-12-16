package com.shaxian.appservice.product;

import com.shaxian.entity.Batch;
import com.shaxian.entity.Color;
import com.shaxian.entity.Product;
import com.shaxian.service.product.ProductBatchCreateService;
import com.shaxian.service.product.ProductBatchDeleteService;
import com.shaxian.service.product.ProductBatchQueryService;
import com.shaxian.service.product.ProductBatchUpdateService;
import com.shaxian.service.product.ProductColorCreateService;
import com.shaxian.service.product.ProductColorDeleteService;
import com.shaxian.service.product.ProductColorQueryService;
import com.shaxian.service.product.ProductColorUpdateService;
import com.shaxian.service.product.ProductCreateService;
import com.shaxian.service.product.ProductDeleteService;
import com.shaxian.service.product.ProductQueryService;
import com.shaxian.service.product.ProductUpdateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductAppService {

    private final ProductQueryService productQueryService;
    private final ProductCreateService productCreateService;
    private final ProductUpdateService productUpdateService;
    private final ProductDeleteService productDeleteService;
    private final ProductColorQueryService colorQueryService;
    private final ProductColorCreateService colorCreateService;
    private final ProductColorUpdateService colorUpdateService;
    private final ProductColorDeleteService colorDeleteService;
    private final ProductBatchQueryService batchQueryService;
    private final ProductBatchCreateService batchCreateService;
    private final ProductBatchUpdateService batchUpdateService;
    private final ProductBatchDeleteService batchDeleteService;

    public ProductAppService(ProductQueryService productQueryService,
                             ProductCreateService productCreateService,
                             ProductUpdateService productUpdateService,
                             ProductDeleteService productDeleteService,
                             ProductColorQueryService colorQueryService,
                             ProductColorCreateService colorCreateService,
                             ProductColorUpdateService colorUpdateService,
                             ProductColorDeleteService colorDeleteService,
                             ProductBatchQueryService batchQueryService,
                             ProductBatchCreateService batchCreateService,
                             ProductBatchUpdateService batchUpdateService,
                             ProductBatchDeleteService batchDeleteService) {
        this.productQueryService = productQueryService;
        this.productCreateService = productCreateService;
        this.productUpdateService = productUpdateService;
        this.productDeleteService = productDeleteService;
        this.colorQueryService = colorQueryService;
        this.colorCreateService = colorCreateService;
        this.colorUpdateService = colorUpdateService;
        this.colorDeleteService = colorDeleteService;
        this.batchQueryService = batchQueryService;
        this.batchCreateService = batchCreateService;
        this.batchUpdateService = batchUpdateService;
        this.batchDeleteService = batchDeleteService;
    }

    // product
    public List<Product> listProducts() {
        return productQueryService.getAll();
    }

    public Optional<Product> findProduct(Long id) {
        return productQueryService.getById(id);
    }

    public Product createProduct(Product product) {
        return productCreateService.create(product);
    }

    public Product updateProduct(Long id, Product product) {
        return productUpdateService.update(id, product);
    }

    public void deleteProduct(Long id) {
        productDeleteService.delete(id);
    }

    // color
    public List<Color> listColors(Long productId) {
        return colorQueryService.findByProductId(productId);
    }

    public Color createColor(Long productId, Color color) {
        return colorCreateService.create(productId, color);
    }

    public Color updateColor(Long id, Color color) {
        return colorUpdateService.update(id, color);
    }

    public void deleteColor(Long id) {
        colorDeleteService.delete(id);
    }

    // batch
    public List<Batch> listBatches(Long colorId) {
        return batchQueryService.findByColorId(colorId);
    }

    public Batch createBatch(Long colorId, Batch batch) {
        return batchCreateService.create(colorId, batch);
    }

    public Batch updateBatch(Long id, Batch batch) {
        return batchUpdateService.update(id, batch);
    }

    public void deleteBatch(Long id) {
        batchDeleteService.delete(id);
    }
}

