package com.shaxian.biz.service.product;

import com.shaxian.biz.entity.Product;
import com.shaxian.biz.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(
            ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product createProduct(Product product) {
        if (productRepository.existsByCode(product.getCode())) {
            throw new IllegalArgumentException("商品编码已存在");
        }
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product product) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在或无权访问"));
        
        // 检查编码是否重复（排除自己）
        if (!existing.getCode().equals(product.getCode()) && 
            productRepository.existsByCode(product.getCode())) {
            throw new IllegalArgumentException("商品编码已存在");
        }
        
        product.setId(id);
        product.setCreatedAt(existing.getCreatedAt());
        return productRepository.save(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("商品不存在或无权访问");
        }
        productRepository.deleteById(id);
    }
}

