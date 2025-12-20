package com.shaxian.service.contact;

import com.shaxian.entity.Customer;
import com.shaxian.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getById(Long id) {
        return customerRepository.findById(id);
    }

    @Transactional
    public Customer create(Customer customer) {
        if (customerRepository.existsByCode(customer.getCode())) {
            throw new IllegalArgumentException("客户编码已存在");
        }
        return customerRepository.save(customer);
    }

    @Transactional
    public Customer update(Long id, Customer customer) {
        Customer existing = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("客户不存在或无权访问"));

        if (!existing.getCode().equals(customer.getCode()) &&
                customerRepository.existsByCode(customer.getCode())) {
            throw new IllegalArgumentException("客户编码已存在");
        }

        customer.setId(id);
        customer.setCreatedAt(existing.getCreatedAt());
        return customerRepository.save(customer);
    }

    @Transactional
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("客户不存在或无权访问");
        }
        customerRepository.deleteById(id);
    }
}
