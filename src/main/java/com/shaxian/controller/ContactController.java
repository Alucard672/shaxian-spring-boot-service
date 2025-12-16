package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.contact.ContactAppService;
import com.shaxian.entity.Customer;
import com.shaxian.entity.Supplier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {

    private final ContactAppService contactAppService;

    public ContactController(ContactAppService contactAppService) {
        this.contactAppService = contactAppService;
    }

    // ========== 客户管理 ==========
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        List<Customer> customers = contactAppService.listCustomers();
        return ResponseEntity.ok(ApiResponse.ok(customers));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomer(@PathVariable Long id) {
        return contactAppService.findCustomer(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.ok(customer)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("客户不存在")));
    }

    @PostMapping("/customers")
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@RequestBody Customer customer) {
        Customer created = contactAppService.createCustomer(customer);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        Customer updated = contactAppService.updateCustomer(id, customer);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        contactAppService.deleteCustomer(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }

    // ========== 供应商管理 ==========
    @GetMapping("/suppliers")
    public ResponseEntity<ApiResponse<List<Supplier>>> getAllSuppliers() {
        List<Supplier> suppliers = contactAppService.listSuppliers();
        return ResponseEntity.ok(ApiResponse.ok(suppliers));
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<ApiResponse<Supplier>> getSupplier(@PathVariable Long id) {
        return contactAppService.findSupplier(id)
                .map(supplier -> ResponseEntity.ok(ApiResponse.ok(supplier)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("供应商不存在")));
    }

    @PostMapping("/suppliers")
    public ResponseEntity<ApiResponse<Supplier>> createSupplier(@RequestBody Supplier supplier) {
        Supplier created = contactAppService.createSupplier(supplier);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<ApiResponse<Supplier>> updateSupplier(@PathVariable Long id, @RequestBody Supplier supplier) {
        Supplier updated = contactAppService.updateSupplier(id, supplier);
        return ResponseEntity.ok(ApiResponse.ok(updated));
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSupplier(@PathVariable Long id) {
        contactAppService.deleteSupplier(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
    }
}

