package com.shaxian.biz.service.product;

import com.shaxian.biz.entity.Product;
import com.shaxian.biz.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ProductQueryService.class);

    private final ProductRepository productRepository;

    public ProductQueryService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public Optional<Product> getById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * 根据ID和租户ID查询商品
     * 用于分享码场景，需要显式指定tenantId，不依赖框架的自动多租户过滤
     *
     * @param id       商品ID
     * @param tenantId 租户ID
     * @return 商品信息
     */
    public Optional<Product> getByIdAndTenantId(Long id, Long tenantId) {
        logger.info("查询商品: id={}, tenantId={}", id, tenantId);
        Optional<Product> result = productRepository.findByIdAndTenantId(id, tenantId);
        logger.info("查询商品结果: id={}, tenantId={}, found={}", id, tenantId, result.isPresent());
        if (result.isPresent()) {
            Product product = result.get();
            logger.info("商品详情: id={}, tenantId={}, name={}, code={}", 
                product.getId(), product.getTenantId(), product.getName(), product.getCode());
        }
        return result;
    }
}

