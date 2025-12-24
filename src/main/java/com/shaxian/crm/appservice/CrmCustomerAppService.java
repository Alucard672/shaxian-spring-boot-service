package com.shaxian.crm.appservice;

import com.shaxian.crm.dto.request.CreateCrmCustomerRequest;
import com.shaxian.crm.dto.request.UpdateCrmCustomerRequest;
import com.shaxian.crm.entity.CrmCustomer;
import com.shaxian.crm.service.CrmCustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrmCustomerAppService {

    private final CrmCustomerService crmCustomerService;

    public CrmCustomerAppService(CrmCustomerService crmCustomerService) {
        this.crmCustomerService = crmCustomerService;
    }

    public List<CrmCustomer> listCustomers() {
        return crmCustomerService.getAll();
    }

    public Optional<CrmCustomer> findCustomer(Long id) {
        return crmCustomerService.getById(id);
    }

    public CrmCustomer createCustomer(CreateCrmCustomerRequest request) {
        CrmCustomer customer = new CrmCustomer();
        customer.setName(request.getName());
        customer.setAddress(request.getAddress());
        customer.setPhone(request.getPhone());
        customer.setRemark(request.getRemark());
        if (request.getSource() != null) {
            customer.setSource(CrmCustomer.CustomerSource.valueOf(request.getSource()));
        }
        if (request.getType() != null) {
            customer.setType(CrmCustomer.CustomerType.valueOf(request.getType()));
        }
        return crmCustomerService.create(customer);
    }

    public CrmCustomer updateCustomer(Long id, UpdateCrmCustomerRequest request) {
        CrmCustomer customer = new CrmCustomer();
        if (request.getName() != null) customer.setName(request.getName());
        if (request.getAddress() != null) customer.setAddress(request.getAddress());
        if (request.getPhone() != null) customer.setPhone(request.getPhone());
        if (request.getRemark() != null) customer.setRemark(request.getRemark());
        if (request.getSource() != null) {
            customer.setSource(CrmCustomer.CustomerSource.valueOf(request.getSource()));
        }
        if (request.getType() != null) {
            customer.setType(CrmCustomer.CustomerType.valueOf(request.getType()));
        }
        return crmCustomerService.update(id, customer);
    }

    public void deleteCustomer(Long id) {
        crmCustomerService.delete(id);
    }
}

