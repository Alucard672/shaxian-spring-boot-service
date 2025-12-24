package com.shaxian.biz.service.product;

import com.shaxian.biz.entity.Color;
import com.shaxian.biz.repository.ColorRepository;
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
