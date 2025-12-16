package com.shaxian.service.product;

import com.shaxian.entity.Batch;
import com.shaxian.repository.BatchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductBatchQueryService {

    private final BatchRepository batchRepository;

    public ProductBatchQueryService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public List<Batch> findByColorId(Long colorId) {
        return batchRepository.findByColorIdOrderByCode(colorId);
    }
}
