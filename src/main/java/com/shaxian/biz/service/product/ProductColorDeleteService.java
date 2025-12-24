package com.shaxian.biz.service.product;

import com.shaxian.biz.repository.ColorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductColorDeleteService {

    private final ColorRepository colorRepository;

    public ProductColorDeleteService(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }

    @Transactional
    public void delete(Long id) {
        if (!colorRepository.existsById(id)) {
            throw new IllegalArgumentException("色号不存在或无权访问");
        }
        colorRepository.deleteById(id);
    }
}
