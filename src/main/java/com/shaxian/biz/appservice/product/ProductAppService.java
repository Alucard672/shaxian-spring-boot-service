package com.shaxian.biz.appservice.product;

import com.shaxian.biz.dto.product.request.CreateBatchRequest;
import com.shaxian.biz.dto.product.request.CreateColorRequest;
import com.shaxian.biz.dto.product.request.CreateProductRequest;
import com.shaxian.biz.dto.product.request.UpdateBatchRequest;
import com.shaxian.biz.dto.product.request.UpdateColorRequest;
import com.shaxian.biz.dto.product.request.UpdateProductRequest;
import com.shaxian.biz.entity.Batch;
import com.shaxian.biz.entity.Color;
import com.shaxian.biz.entity.Product;
import com.shaxian.biz.service.product.ProductBatchCreateService;
import com.shaxian.biz.service.product.ProductBatchDeleteService;
import com.shaxian.biz.service.product.ProductBatchQueryService;
import com.shaxian.biz.service.product.ProductBatchUpdateService;
import com.shaxian.biz.service.product.ProductColorCreateService;
import com.shaxian.biz.service.product.ProductColorDeleteService;
import com.shaxian.biz.service.product.ProductColorQueryService;
import com.shaxian.biz.service.product.ProductColorUpdateService;
import com.shaxian.biz.service.product.ProductCreateService;
import com.shaxian.biz.service.product.ProductDeleteService;
import com.shaxian.biz.dto.product.response.ShareCodeResponse;
import com.shaxian.biz.service.product.ProductQueryService;
import com.shaxian.biz.service.product.ProductShareCodeService;
import com.shaxian.biz.service.product.ProductUpdateService;
import com.shaxian.biz.util.TenantContext;
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
    private final ProductShareCodeService productShareCodeService;

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
                             ProductBatchDeleteService batchDeleteService,
                             ProductShareCodeService productShareCodeService) {
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
        this.productShareCodeService = productShareCodeService;
    }

    // product
    public List<Product> listProducts() {
        return productQueryService.getAll();
    }

    public Optional<Product> findProduct(Long id) {
        return productQueryService.getById(id);
    }

    public Product createProduct(CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setCode(request.getCode());
        product.setSpecification(request.getSpecification());
        product.setComposition(request.getComposition());
        product.setCount(request.getCount());
        product.setUnit(request.getUnit() != null ? request.getUnit() : "kg");
        if (request.getType() != null) {
            product.setType(Product.ProductType.valueOf(request.getType()));
        }
        product.setIsWhiteYarn(request.getIsWhiteYarn());
        product.setDescription(request.getDescription());
        return productCreateService.create(product);
    }

    public Product updateProduct(Long id, UpdateProductRequest request) {
        Product product = new Product();
        if (request.getName() != null) product.setName(request.getName());
        if (request.getCode() != null) product.setCode(request.getCode());
        if (request.getSpecification() != null) product.setSpecification(request.getSpecification());
        if (request.getComposition() != null) product.setComposition(request.getComposition());
        if (request.getCount() != null) product.setCount(request.getCount());
        if (request.getUnit() != null) product.setUnit(request.getUnit());
        if (request.getType() != null) {
            product.setType(Product.ProductType.valueOf(request.getType()));
        }
        if (request.getIsWhiteYarn() != null) product.setIsWhiteYarn(request.getIsWhiteYarn());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        return productUpdateService.update(id, product);
    }

    public void deleteProduct(Long id) {
        productDeleteService.delete(id);
    }

    // color
    public List<Color> listColors(Long productId) {
        return colorQueryService.findByProductId(productId);
    }

    public Color createColor(Long productId, CreateColorRequest request) {
        Color color = new Color();
        color.setProductId(productId);
        color.setCode(request.getCode());
        color.setName(request.getName());
        color.setColorValue(request.getColorValue());
        color.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            color.setStatus(Color.ColorStatus.valueOf(request.getStatus()));
        }
        return colorCreateService.create(productId, color);
    }

    public Color updateColor(Long id, UpdateColorRequest request) {
        Color color = new Color();
        if (request.getCode() != null) color.setCode(request.getCode());
        if (request.getName() != null) color.setName(request.getName());
        if (request.getColorValue() != null) color.setColorValue(request.getColorValue());
        if (request.getDescription() != null) color.setDescription(request.getDescription());
        if (request.getStatus() != null) {
            color.setStatus(Color.ColorStatus.valueOf(request.getStatus()));
        }
        return colorUpdateService.update(id, color);
    }

    public void deleteColor(Long id) {
        colorDeleteService.delete(id);
    }

    // batch
    public List<Batch> listBatches(Long colorId) {
        return batchQueryService.findByColorId(colorId);
    }

    public Batch createBatch(Long colorId, CreateBatchRequest request) {
        Batch batch = new Batch();
        batch.setColorId(colorId);
        batch.setCode(request.getCode());
        batch.setProductionDate(request.getProductionDate());
        batch.setSupplierId(request.getSupplierId());
        batch.setSupplierName(request.getSupplierName());
        batch.setPurchasePrice(request.getPurchasePrice());
        batch.setInitialQuantity(request.getInitialQuantity());
        batch.setStockLocation(request.getStockLocation());
        batch.setRemark(request.getRemark());
        return batchCreateService.create(colorId, batch);
    }

    public Batch updateBatch(Long id, UpdateBatchRequest request) {
        Batch batch = new Batch();
        if (request.getCode() != null) batch.setCode(request.getCode());
        if (request.getProductionDate() != null) batch.setProductionDate(request.getProductionDate());
        if (request.getSupplierId() != null) batch.setSupplierId(request.getSupplierId());
        if (request.getSupplierName() != null) batch.setSupplierName(request.getSupplierName());
        if (request.getPurchasePrice() != null) batch.setPurchasePrice(request.getPurchasePrice());
        if (request.getInitialQuantity() != null) batch.setInitialQuantity(request.getInitialQuantity());
        if (request.getStockLocation() != null) batch.setStockLocation(request.getStockLocation());
        if (request.getRemark() != null) batch.setRemark(request.getRemark());
        return batchUpdateService.update(id, batch);
    }

    public void deleteBatch(Long id) {
        batchDeleteService.delete(id);
    }

    // share code
    /**
     * 生成商品分享码
     *
     * @param productId 商品ID
     * @param tenantId  租户ID
     * @return 分享码响应
     */
    public ShareCodeResponse generateShareCode(Long productId, Long tenantId) {
        String shareCode = productShareCodeService.generateShareCode(productId, tenantId);
        return new ShareCodeResponse(shareCode);
    }

    /**
     * 根据分享码获取商品详情
     *
     * @param shareCode 分享码
     * @return 商品详情
     */
    public Optional<Product> getProductByShareCode(String shareCode) {
        // 验证分享码并获取商品ID和租户ID
        ProductShareCodeService.ShareCodeVerificationResult result = productShareCodeService.verifyAndGetProductId(shareCode);

        // 设置租户上下文，以便正确查询多租户数据
        TenantContext.setTenantId(result.getTenantId());

        try {
            // 查询商品详情
            return productQueryService.getById(result.getProductId());
        } finally {
            // 清理租户上下文
            TenantContext.clear();
        }
    }
}

