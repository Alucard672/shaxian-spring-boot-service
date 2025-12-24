package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmCustomer;
import com.shaxian.crm.repository.CrmCustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CrmCustomerService {

    private final CrmCustomerRepository crmCustomerRepository;

    public CrmCustomerService(CrmCustomerRepository crmCustomerRepository) {
        this.crmCustomerRepository = crmCustomerRepository;
    }

    public List<CrmCustomer> getAll() {
        return crmCustomerRepository.findAll();
    }

    public Optional<CrmCustomer> getById(Long id) {
        return crmCustomerRepository.findById(id);
    }

    @Transactional
    public CrmCustomer create(CrmCustomer customer) {
        if (crmCustomerRepository.existsByPhone(customer.getPhone())) {
            throw new IllegalArgumentException("手机号已存在");
        }
        return crmCustomerRepository.save(customer);
    }

    @Transactional
    public CrmCustomer update(Long id, CrmCustomer customer) {
        CrmCustomer existing = crmCustomerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));

        if (customer.getPhone() != null && !existing.getPhone().equals(customer.getPhone()) &&
                crmCustomerRepository.existsByPhone(customer.getPhone())) {
            throw new IllegalArgumentException("手机号已存在");
        }

        // 只更新非 null 的字段
        if (customer.getName() != null) {
            existing.setName(customer.getName());
        }
        if (customer.getAddress() != null) {
            existing.setAddress(customer.getAddress());
        }
        if (customer.getPhone() != null) {
            existing.setPhone(customer.getPhone());
        }
        if (customer.getRemark() != null) {
            existing.setRemark(customer.getRemark());
        }
        if (customer.getSource() != null) {
            existing.setSource(customer.getSource());
        }
        if (customer.getType() != null) {
            existing.setType(customer.getType());
        }

        return crmCustomerRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        if (!crmCustomerRepository.existsById(id)) {
            throw new IllegalArgumentException("客户不存在");
        }
        crmCustomerRepository.deleteById(id);
    }
}

