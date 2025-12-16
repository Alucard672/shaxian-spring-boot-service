package com.shaxian.service.product;

import com.shaxian.repository.ColorRepository;
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
            throw new IllegalArgumentException("色号不存在");
        }
        colorRepository.deleteById(id);
    }
}
