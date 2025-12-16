package com.shaxian.service.auth;

import com.shaxian.entity.Employee;
import com.shaxian.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final EmployeeRepository employeeRepository;

    public AuthService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /**
     * 登录校验，返回通过校验的员工信息，否则抛出业务异常
     */
    public Employee login(String phone, String password) {
        Employee employee = employeeRepository.findByPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // 检查密码（默认密码123456）
        if (employee.getPassword() == null || employee.getPassword().isEmpty()) {
            if (!"123456".equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        } else {
            if (!employee.getPassword().equals(password)) {
                throw new IllegalArgumentException("密码错误");
            }
        }

        // 检查员工状态
        if (employee.getStatus() != Employee.EmployeeStatus.active) {
            throw new IllegalArgumentException("账户已被禁用");
        }

        return employee;
    }
}

