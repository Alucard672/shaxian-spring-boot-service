package com.shaxian.service.product;

import com.shaxian.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductDeleteService {

    private final ProductRepository productRepository;

    public ProductDeleteService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("商品不存在");
        }
        productRepository.deleteById(id);
    }
}

