package com.shaxian.service.product;

import com.shaxian.repository.BatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductBatchDeleteService {

    private final BatchRepository batchRepository;

    public ProductBatchDeleteService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Transactional
    public void delete(Long id) {
        if (!batchRepository.existsById(id)) {
            throw new IllegalArgumentException("缸号不存在或无权访问");
        }
        batchRepository.deleteById(id);
    }
}
