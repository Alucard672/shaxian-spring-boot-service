package com.shaxian.biz.appservice.employee;

import com.shaxian.biz.dto.employee.request.CreateEmployeeRequest;
import com.shaxian.biz.dto.employee.request.UpdateEmployeeRequest;
import com.shaxian.biz.entity.Employee;
import com.shaxian.biz.entity.User;
import com.shaxian.biz.service.settings.EmployeeService;
import com.shaxian.biz.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeAppService {

    private final EmployeeService employeeService;
    private final UserService userService;

    public EmployeeAppService(EmployeeService employeeService, UserService userService) {
        this.employeeService = employeeService;
        this.userService = userService;
    }

    public List<Employee> listEmployees() {
        return employeeService.getAll();
    }

    public Optional<Employee> findEmployee(Long id) {
        return employeeService.getById(id);
    }

    public Employee createEmployee(CreateEmployeeRequest request) {
        Employee employee = new Employee();
        employee.setName(request.getName());
        employee.setPosition(request.getPosition());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setRole(request.getRole());
        employee.setPassword(request.getPassword());
        if (request.getStatus() != null) {
            employee.setStatus(Employee.EmployeeStatus.valueOf(request.getStatus()));
        }
        return employeeService.create(employee);
    }

    public Employee updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = new Employee();
        if (request.getName() != null) employee.setName(request.getName());
        if (request.getPosition() != null) employee.setPosition(request.getPosition());
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getEmail() != null) employee.setEmail(request.getEmail());
        if (request.getRole() != null) employee.setRole(request.getRole());
        if (request.getPassword() != null) employee.setPassword(request.getPassword());
        if (request.getStatus() != null) {
            employee.setStatus(Employee.EmployeeStatus.valueOf(request.getStatus()));
        }
        return employeeService.update(id, employee);
    }

    public void deleteEmployee(Long id) {
        employeeService.delete(id);
    }

    /**
     * 授权员工登录
     * 将员工数据同步到用户表，并建立租户关联
     */
    public User authorizeEmployeeLogin(Long employeeId, Long tenantId) {
        return userService.authorizeEmployeeLogin(employeeId, tenantId);
    }
}
