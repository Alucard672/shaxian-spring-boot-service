package com.shaxian.service.product;

import com.shaxian.entity.Batch;
import com.shaxian.repository.BatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductBatchUpdateService {

    private final BatchRepository batchRepository;

    public ProductBatchUpdateService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Transactional
    public Batch update(Long id, Batch batch) {
        if (!batchRepository.existsById(id)) {
            throw new IllegalArgumentException("缸号不存在");
        }
        Batch existing = batchRepository.findById(id).orElseThrow();
        batch.setId(id);
        batch.setColorId(existing.getColorId());
        batch.setCreatedAt(existing.getCreatedAt());
        return batchRepository.save(batch);
    }
}
