package com.shaxian.service.contact;

import com.shaxian.entity.Supplier;
import com.shaxian.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<Supplier> getAll() {
        return supplierRepository.findAll();
    }

    public Optional<Supplier> getById(Long id) {
        return supplierRepository.findById(id);
    }

    @Transactional
    public Supplier create(Supplier supplier) {
        if (supplierRepository.existsByCode(supplier.getCode())) {
            throw new IllegalArgumentException("供应商编码已存在");
        }
        return supplierRepository.save(supplier);
    }

    @Transactional
    public Supplier update(Long id, Supplier supplier) {
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("供应商不存在或无权访问"));

        if (!existing.getCode().equals(supplier.getCode()) &&
                supplierRepository.existsByCode(supplier.getCode())) {
            throw new IllegalArgumentException("供应商编码已存在");
        }

        supplier.setId(id);
        supplier.setCreatedAt(existing.getCreatedAt());
        return supplierRepository.save(supplier);
    }

    @Transactional
    public void delete(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new IllegalArgumentException("供应商不存在或无权访问");
        }
        supplierRepository.deleteById(id);
    }
}
