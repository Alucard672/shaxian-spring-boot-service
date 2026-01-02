package com.shaxian.crm.appservice;

import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.appservice.tenant.TenantAppService;
import com.shaxian.biz.dto.tenant.request.CreateTenantRequest;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.crm.dto.request.CreateCrmSalesOrderRequest;
import com.shaxian.crm.dto.request.CrmSalesOrderQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmSalesOrderRequest;
import com.shaxian.crm.entity.CrmCustomer;
import com.shaxian.crm.entity.CrmSalesOrder;
import com.shaxian.crm.entity.CrmSalesOrderItem;
import com.shaxian.crm.repository.CrmCustomerRepository;
import com.shaxian.crm.service.CrmCustomerService;
import com.shaxian.crm.service.CrmSalesService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CrmSalesAppService {

    private final CrmSalesService crmSalesService;
    private final CrmCustomerService crmCustomerService;
    private final CrmCustomerRepository crmCustomerRepository;
    private final TenantRepository tenantRepository;
    private final TenantAppService tenantAppService;

    public CrmSalesAppService(
            CrmSalesService crmSalesService,
            CrmCustomerService crmCustomerService,
            CrmCustomerRepository crmCustomerRepository,
            TenantRepository tenantRepository,
            TenantAppService tenantAppService) {
        this.crmSalesService = crmSalesService;
        this.crmCustomerService = crmCustomerService;
        this.crmCustomerRepository = crmCustomerRepository;
        this.tenantRepository = tenantRepository;
        this.tenantAppService = tenantAppService;
    }

    @Transactional
    public CrmSalesOrder createSalesOrder(CreateCrmSalesOrderRequest request) {
        // 获取CRM客户信息
        CrmCustomer customer = crmCustomerRepository.findById(request.getCrmCustomerId())
                .orElseThrow(() -> new IllegalArgumentException("CRM客户不存在"));

        // 检查客户类型，如果是潜在客户且没有租户，则创建租户
        if (customer.getType() == CrmCustomer.CustomerType.POTENTIAL) {
            Optional<Tenant> existingTenant = tenantRepository.findByCrmCustomerId(request.getCrmCustomerId());
            if (existingTenant.isEmpty()) {
                // 创建租户
                CreateTenantRequest tenantRequest = new CreateTenantRequest();
                tenantRequest.setName(customer.getName());
                tenantRequest.setAddress(customer.getAddress() != null ? customer.getAddress() : "");
                
                // 创建租户，传入crmCustomerId，不关联用户（userId传null）
                tenantAppService.createTenant(tenantRequest, null, request.getCrmCustomerId());
                
                // 更新客户类型为正式客户
                crmCustomerService.updateCustomerType(request.getCrmCustomerId(), CrmCustomer.CustomerType.OFFICIAL);
            }
        }

        // 构建订单实体
        CrmSalesOrder order = new CrmSalesOrder();
        order.setCrmCustomerId(request.getCrmCustomerId());
        order.setCustomerName(request.getCustomerName());
        order.setSalesDate(request.getSalesDate());
        order.setPaidAmount(request.getPaidAmount() != null ? request.getPaidAmount() : java.math.BigDecimal.ZERO);
        order.setOperator(request.getOperator());
        order.setRemark(request.getRemark());

        // 构建订单项
        List<CrmSalesOrderItem> items = request.getItems().stream().map(itemRequest -> {
            CrmSalesOrderItem item = new CrmSalesOrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setProductCode(itemRequest.getProductCode());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setQuantity(itemRequest.getQuantity());
            item.setRemark(itemRequest.getRemark());
            return item;
        }).toList();

        // 创建订单
        return crmSalesService.createSales(order, items);
    }

    public PageResult<CrmSalesOrder> querySalesOrders(CrmSalesOrderQueryRequest request, Integer pageNo, Integer pageSize) {
        Page<CrmSalesOrder> page = crmSalesService.querySales(
                request.getCrmCustomerId(),
                request.getStatus(),
                request.getStartDate(),
                request.getEndDate(),
                pageNo,
                pageSize
        );
        return PageResult.of(page.getContent(), page.getTotalElements(), pageNo, pageSize);
    }

    public Optional<CrmSalesOrder> getSalesOrder(Long id) {
        return crmSalesService.getSalesById(id);
    }

    @Transactional
    public CrmSalesOrder updateSalesOrder(Long id, UpdateCrmSalesOrderRequest request) {
        // 构建订单实体
        CrmSalesOrder order = new CrmSalesOrder();
        if (request.getCustomerName() != null) order.setCustomerName(request.getCustomerName());
        if (request.getSalesDate() != null) order.setSalesDate(request.getSalesDate());
        if (request.getPaidAmount() != null) order.setPaidAmount(request.getPaidAmount());
        if (request.getOperator() != null) order.setOperator(request.getOperator());
        if (request.getRemark() != null) order.setRemark(request.getRemark());
        if (request.getStatus() != null) {
            try {
                order.setStatus(CrmSalesOrder.OrderStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的状态值: " + request.getStatus());
            }
        }

        // 构建订单项
        List<CrmSalesOrderItem> items = request.getItems().stream().map(itemRequest -> {
            CrmSalesOrderItem item = new CrmSalesOrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setProductCode(itemRequest.getProductCode());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setQuantity(itemRequest.getQuantity());
            item.setRemark(itemRequest.getRemark());
            return item;
        }).toList();

        return crmSalesService.updateSales(id, order, items);
    }

    @Transactional
    public void deleteSalesOrder(Long id) {
        crmSalesService.deleteSales(id);
    }
}

