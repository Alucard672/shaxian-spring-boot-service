package com.shaxian.controller;

import com.shaxian.api.ApiResponse;
import com.shaxian.appservice.employee.EmployeeAppService;
import com.shaxian.dto.employee.request.CreateEmployeeRequest;
import com.shaxian.dto.employee.request.UpdateEmployeeRequest;
import com.shaxian.entity.Employee;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeAppService employeeAppService;

    public EmployeeController(EmployeeAppService employeeAppService) {
        this.employeeAppService = employeeAppService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Employee>>> getAllEmployees() {
        List<Employee> employees = employeeAppService.listEmployees();
        return ResponseEntity.ok(ApiResponse.ok(employees));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployee(@PathVariable Long id) {
        return employeeAppService.findEmployee(id)
                .map(employee -> ResponseEntity.ok(ApiResponse.ok(employee)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.fail("员工不存在")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> createEmployee(@RequestBody CreateEmployeeRequest request) {
        Employee created = employeeAppService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(@PathVariable Long id, @RequestBody UpdateEmployeeRequest request) {
        try {
            Employee updated = employeeAppService.updateEmployee(id, request);
            return ResponseEntity.ok(ApiResponse.ok(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        try {
            employeeAppService.deleteEmployee(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.fail(e.getMessage()));
        }
    }
}
