package com.shaxian.biz.service.product;

import com.shaxian.biz.entity.Batch;
import com.shaxian.biz.repository.BatchRepository;
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
