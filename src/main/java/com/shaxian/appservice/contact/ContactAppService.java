package com.shaxian.appservice.contact;

import com.shaxian.entity.Customer;
import com.shaxian.entity.Supplier;
import com.shaxian.service.contact.CustomerService;
import com.shaxian.service.contact.SupplierService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactAppService {

    private final CustomerService customerService;
    private final SupplierService supplierService;

    public ContactAppService(CustomerService customerService, SupplierService supplierService) {
        this.customerService = customerService;
        this.supplierService = supplierService;
    }

    // customer
    public List<Customer> listCustomers() {
        return customerService.getAll();
    }

    public Optional<Customer> findCustomer(Long id) {
        return customerService.getById(id);
    }

    public Customer createCustomer(Customer customer) {
        return customerService.create(customer);
    }

    public Customer updateCustomer(Long id, Customer customer) {
        return customerService.update(id, customer);
    }

    public void deleteCustomer(Long id) {
        customerService.delete(id);
    }

    // supplier
    public List<Supplier> listSuppliers() {
        return supplierService.getAll();
    }

    public Optional<Supplier> findSupplier(Long id) {
        return supplierService.getById(id);
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierService.create(supplier);
    }

    public Supplier updateSupplier(Long id, Supplier supplier) {
        return supplierService.update(id, supplier);
    }

    public void deleteSupplier(Long id) {
        supplierService.delete(id);
    }
}
