package com.shaxian.crm.service;

import com.shaxian.crm.entity.CrmCustomer;
import com.shaxian.crm.repository.CrmCustomerRepository;
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
public class CrmCustomerService {

    private final CrmCustomerRepository crmCustomerRepository;

    public CrmCustomerService(CrmCustomerRepository crmCustomerRepository) {
        this.crmCustomerRepository = crmCustomerRepository;
    }

    public List<CrmCustomer> getAll() {
        return crmCustomerRepository.findAll();
    }

    public Page<CrmCustomer> queryCustomers(String name, String phone, String source, String type, Integer pageNo, Integer pageSize) {
        Specification<CrmCustomer> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(name)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(phone)) {
                predicates.add(cb.like(root.get("phone"), "%" + phone + "%"));
            }
            if (StringUtils.hasText(source)) {
                try {
                    CrmCustomer.CustomerSource sourceEnum = CrmCustomer.CustomerSource.valueOf(source);
                    predicates.add(cb.equal(root.get("source"), sourceEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的枚举值
                }
            }
            if (StringUtils.hasText(type)) {
                try {
                    CrmCustomer.CustomerType typeEnum = CrmCustomer.CustomerType.valueOf(type);
                    predicates.add(cb.equal(root.get("type"), typeEnum));
                } catch (IllegalArgumentException e) {
                    // 忽略无效的枚举值
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        return crmCustomerRepository.findAll(spec, pageable);
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

    @Transactional
    public void updateCustomerType(Long id, CrmCustomer.CustomerType type) {
        CrmCustomer customer = crmCustomerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        customer.setType(type);
        crmCustomerRepository.save(customer);
    }

    /**
     * 为用户注册创建潜在客户记录
     * 如果客户已存在则跳过，如果不存在则创建新的潜在客户
     *
     * @param phone 手机号
     * @param name 客户名称（可选，如果为空则使用手机号）
     * @return 创建的客户或已存在的客户，如果创建失败返回null
     */
    @Transactional
    public Optional<CrmCustomer> createPotentialCustomerForRegistration(String phone, String name) {
        if (phone == null || phone.trim().isEmpty()) {
            return Optional.empty();
        }

        // 检查是否已存在
        Optional<CrmCustomer> existing = crmCustomerRepository.findByPhone(phone);
        if (existing.isPresent()) {
            return existing;
        }

        // 创建新的潜在客户
        try {
            CrmCustomer customer = new CrmCustomer();
            customer.setName(name != null && !name.trim().isEmpty() ? name : phone);
            customer.setPhone(phone);
            customer.setSource(CrmCustomer.CustomerSource.ONLINE);
            customer.setType(CrmCustomer.CustomerType.POTENTIAL);
            customer.setRemark("用户自主注册");
            return Optional.of(crmCustomerRepository.save(customer));
        } catch (Exception e) {
            // 创建失败时返回空，不影响注册流程
            return Optional.empty();
        }
    }
}

