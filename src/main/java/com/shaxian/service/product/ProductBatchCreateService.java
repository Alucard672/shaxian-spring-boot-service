package com.shaxian.service.product;

import com.shaxian.entity.Batch;
import com.shaxian.repository.BatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class ProductBatchCreateService {

    private final BatchRepository batchRepository;

    public ProductBatchCreateService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    @Transactional
    public Batch create(Long colorId, Batch batch) {
        batch.setColorId(colorId);
        if (batch.getStockQuantity() == null) {
            batch.setStockQuantity(batch.getInitialQuantity() != null ? batch.getInitialQuantity() : BigDecimal.ZERO);
        }
        return batchRepository.save(batch);
    }
}
