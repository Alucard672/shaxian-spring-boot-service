package com.shaxian.biz.appservice.settings;

import com.shaxian.biz.entity.*;
import com.shaxian.biz.service.settings.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SettingsAppService {

    private final StoreInfoService storeInfoService;
    private final EmployeeService employeeService;
    private final RoleService roleService;
    private final CustomQueryService customQueryService;
    private final InventoryAlertSettingsService inventoryAlertSettingsService;
    private final SystemParamsService systemParamsService;

    public SettingsAppService(
            StoreInfoService storeInfoService,
            EmployeeService employeeService,
            RoleService roleService,
            CustomQueryService customQueryService,
            InventoryAlertSettingsService inventoryAlertSettingsService,
            SystemParamsService systemParamsService) {
        this.storeInfoService = storeInfoService;
        this.employeeService = employeeService;
        this.roleService = roleService;
        this.customQueryService = customQueryService;
        this.inventoryAlertSettingsService = inventoryAlertSettingsService;
        this.systemParamsService = systemParamsService;
    }

    // 门店信息
    public StoreInfo getStoreInfo() {
        return storeInfoService.getStoreInfo();
    }

    public StoreInfo updateStoreInfo(Map<String, Object> request) {
        return storeInfoService.updateStoreInfo(request);
    }

    // 员工管理
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

    // 角色管理
    public List<Role> listRoles() {
        return roleService.getAll();
    }

    public Role createRole(Map<String, Object> request) throws Exception {
        return roleService.create(request);
    }

    public Role updateRole(Long id, Map<String, Object> request) throws Exception {
        return roleService.update(id, request);
    }

    public void deleteRole(Long id) {
        roleService.delete(id);
    }

    // 自定义查询
    public List<CustomQuery> listQueries(String module) {
        return customQueryService.getAll(module);
    }

    public CustomQuery createQuery(Map<String, Object> request) throws Exception {
        return customQueryService.create(request);
    }

    // 库存预警设置
    public InventoryAlertSettings getInventoryAlertSettings() {
        return inventoryAlertSettingsService.getSettings();
    }

    public InventoryAlertSettings updateInventoryAlertSettings(Map<String, Object> request) {
        return inventoryAlertSettingsService.updateSettings(request);
    }

    // 系统参数
    public SystemParams getSystemParams() {
        return systemParamsService.getSystemParams();
    }

    public SystemParams updateSystemParams(Map<String, Object> request) {
        return systemParamsService.updateSystemParams(request);
    }
}
