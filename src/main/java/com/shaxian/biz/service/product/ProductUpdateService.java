package com.shaxian.biz.service.product;

import com.shaxian.biz.entity.Product;
import com.shaxian.biz.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductUpdateService {

    private final ProductRepository productRepository;

    public ProductUpdateService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product update(Long id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在或无权访问"));

        if (!existing.getCode().equals(product.getCode())
                && productRepository.existsByCode(product.getCode())) {
            throw new IllegalArgumentException("商品编码已存在");
        }

        product.setId(id);
        product.setCreatedAt(existing.getCreatedAt());
        return productRepository.save(product);
    }
}

