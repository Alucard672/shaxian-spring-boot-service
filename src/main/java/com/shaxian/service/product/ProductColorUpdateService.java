package com.shaxian.service.product;

import com.shaxian.entity.Color;
import com.shaxian.repository.ColorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductColorUpdateService {

    private final ColorRepository colorRepository;

    public ProductColorUpdateService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Transactional
    public Color update(Long id, Color color) {
        Color existing = colorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("色号不存在或无权访问"));
        color.setId(id);
        color.setProductId(existing.getProductId());
        color.setCreatedAt(existing.getCreatedAt());
        return colorRepository.save(color);
    }
}
