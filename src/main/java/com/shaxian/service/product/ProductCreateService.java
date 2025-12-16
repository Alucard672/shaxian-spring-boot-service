package com.shaxian.service.product;

import com.shaxian.entity.Product;
import com.shaxian.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductCreateService {

    private final ProductRepository productRepository;

    public ProductCreateService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public Product create(Product product) {
        if (productRepository.existsByCode(product.getCode())) {
            throw new IllegalArgumentException("商品编码已存在");
        }
        return productRepository.save(product);
    }
}

