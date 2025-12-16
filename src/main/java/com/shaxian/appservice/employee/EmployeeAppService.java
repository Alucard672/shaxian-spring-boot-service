package com.shaxian.appservice.employee;

import com.shaxian.entity.Employee;
import com.shaxian.service.settings.EmployeeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeAppService {

    private final EmployeeService employeeService;

    public EmployeeAppService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    public List<Employee> listEmployees() {
        return employeeService.getAll();
    }

    public Optional<Employee> findEmployee(Long id) {
        return employeeService.getById(id);
    }

    public Employee createEmployee(Employee employee) {
        return employeeService.create(employee);
    }

    public Employee updateEmployee(Long id, Employee employee) {
        return employeeService.update(id, employee);
    }

    public void deleteEmployee(Long id) {
        employeeService.delete(id);
    }
}
