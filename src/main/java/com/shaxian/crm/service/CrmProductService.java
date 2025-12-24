package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmProduct;
import com.shaxian.crm.repository.CrmProductRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CrmProductService {

    private final CrmProductRepository crmProductRepository;

    public CrmProductService(CrmProductRepository crmProductRepository) {
        this.crmProductRepository = crmProductRepository;
    }

    public List<CrmProduct> getAll() {
        return crmProductRepository.findAll();
    }

    public Page<CrmProduct> queryProducts(String name, String code, String status, Integer pageNo, Integer pageSize) {
        Specification<CrmProduct> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(code)) {
                predicates.add(cb.like(root.get("code"), "%" + code + "%"));
            }
            if (StringUtils.hasText(status)) {
                try {
                    CrmProduct.ProductStatus statusEnum = CrmProduct.ProductStatus.valueOf(status);
                    predicates.add(cb.equal(root.get("status"), statusEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的枚举值
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return crmProductRepository.findAll(spec, pageable);
    }

    public Optional<CrmProduct> getById(Long id) {
        return crmProductRepository.findById(id);
    }

    @Transactional
    public CrmProduct create(CrmProduct product) {
        if (crmProductRepository.existsByCode(product.getCode())) {
            throw new IllegalArgumentException("商品编码已存在");
        }
        return crmProductRepository.save(product);
    }

    @Transactional
    public CrmProduct update(Long id, CrmProduct product) {
        CrmProduct existing = crmProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));

        if (product.getCode() != null && !existing.getCode().equals(product.getCode()) &&
                crmProductRepository.existsByCode(product.getCode())) {
            throw new IllegalArgumentException("商品编码已存在");
        }

        // 只更新非 null 的字段
        if (product.getName() != null) {
            existing.setName(product.getName());
        }
        if (product.getCode() != null) {
            existing.setCode(product.getCode());
        }
        if (product.getUnitPrice() != null) {
            existing.setUnitPrice(product.getUnitPrice());
        }
        if (product.getDiscountPrice() != null) {
            existing.setDiscountPrice(product.getDiscountPrice());
        }
        if (product.getProductValue() != null) {
            existing.setProductValue(product.getProductValue());
        }
        if (product.getLicenseCount() != null) {
            existing.setLicenseCount(product.getLicenseCount());
        }
        if (product.getStatus() != null) {
            existing.setStatus(product.getStatus());
        }
        if (product.getDescription() != null) {
            existing.setDescription(product.getDescription());
        }

        return crmProductRepository.save(existing);
    }

    @Transactional
    public CrmProduct activate(Long id) {
        CrmProduct product = crmProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        product.setStatus(CrmProduct.ProductStatus.ACTIVE);
        return crmProductRepository.save(product);
    }

    @Transactional
    public CrmProduct deactivate(Long id) {
        CrmProduct product = crmProductRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("商品不存在"));
        product.setStatus(CrmProduct.ProductStatus.INACTIVE);
        return crmProductRepository.save(product);
    }
}

