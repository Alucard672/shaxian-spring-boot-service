package com.shaxian.service.product;

import com.shaxian.entity.Color;
import com.shaxian.repository.ColorRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductColorQueryService {

    private final ColorRepository colorRepository;

    public ProductColorQueryService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    public List<Color> findByProductId(Long productId) {
        return colorRepository.findByProductIdOrderByCode(productId);
    }
}
