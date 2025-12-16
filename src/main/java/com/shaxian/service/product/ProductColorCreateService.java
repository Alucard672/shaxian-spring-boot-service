package com.shaxian.service.product;

import com.shaxian.entity.Color;
import com.shaxian.repository.ColorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductColorCreateService {

    private final ColorRepository colorRepository;

    public ProductColorCreateService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Transactional
    public Color create(Long productId, Color color) {
        color.setProductId(productId);
        return colorRepository.save(color);
    }
}
