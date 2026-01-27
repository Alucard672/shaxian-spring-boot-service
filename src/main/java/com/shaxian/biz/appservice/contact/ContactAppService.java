package com.shaxian.biz.appservice.contact;

import com.shaxian.biz.dto.contact.request.CreateCustomerRequest;
import com.shaxian.biz.dto.contact.request.CreateSupplierRequest;
import com.shaxian.biz.dto.contact.request.UpdateCustomerRequest;
import com.shaxian.biz.dto.contact.request.UpdateSupplierRequest;
import com.shaxian.biz.entity.Customer;
import com.shaxian.biz.entity.Supplier;
import com.shaxian.biz.service.contact.CustomerService;
import com.shaxian.biz.service.contact.SupplierService;
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

    public Customer createCustomer(CreateCustomerRequest request) {
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setContactPerson(request.getContactPerson());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        if (request.getType() != null) {
            customer.setType(Customer.CustomerType.valueOf(request.getType()));
        }
        customer.setCreditLimit(request.getCreditLimit());
        if (request.getStatus() != null) {
            customer.setStatus(Customer.CustomerStatus.valueOf(request.getStatus()));
        }
        customer.setRemark(request.getRemark());
        return customerService.create(customer);
    }

    public Customer updateCustomer(Long id, UpdateCustomerRequest request) {
        Customer customer = new Customer();
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getContactPerson() != null) customer.setContactPerson(request.getContactPerson());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getType() != null) {
            customer.setType(Customer.CustomerType.valueOf(request.getType()));
        }
        if (request.getCreditLimit() != null) customer.setCreditLimit(request.getCreditLimit());
        if (request.getStatus() != null) {
            customer.setStatus(Customer.CustomerStatus.valueOf(request.getStatus()));
        }
        if (request.getRemark() != null) customer.setRemark(request.getRemark());
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

    public Supplier createSupplier(CreateSupplierRequest request) {
        Supplier supplier = new Supplier();
        supplier.setName(request.getName());
        supplier.setCode(request.getCode());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());
        if (request.getType() != null) {
            supplier.setType(Supplier.SupplierType.valueOf(request.getType()));
        }
        if (request.getSettlementCycle() != null) {
            supplier.setSettlementCycle(Supplier.SettlementCycle.valueOf(request.getSettlementCycle()));
        }
        if (request.getStatus() != null) {
            supplier.setStatus(Supplier.SupplierStatus.valueOf(request.getStatus()));
        }
        supplier.setRemark(request.getRemark());
        return supplierService.create(supplier);
    }

    public Supplier updateSupplier(Long id, UpdateSupplierRequest request) {
        Supplier supplier = new Supplier();
        if (request.getName() != null) supplier.setName(request.getName());
        if (request.getCode() != null) supplier.setCode(request.getCode());
        if (request.getContactPerson() != null) supplier.setContactPerson(request.getContactPerson());
        if (request.getPhone() != null) supplier.setPhone(request.getPhone());
        if (request.getAddress() != null) supplier.setAddress(request.getAddress());
        if (request.getType() != null) {
            supplier.setType(Supplier.SupplierType.valueOf(request.getType()));
        }
        if (request.getSettlementCycle() != null) {
            supplier.setSettlementCycle(Supplier.SettlementCycle.valueOf(request.getSettlementCycle()));
        }
        if (request.getStatus() != null) {
            supplier.setStatus(Supplier.SupplierStatus.valueOf(request.getStatus()));
        }
        if (request.getRemark() != null) supplier.setRemark(request.getRemark());
        return supplierService.update(id, supplier);
    }

    public void deleteSupplier(Long id) {
        supplierService.delete(id);
    }
}
