package com.shaxian.crm.appservice;

import com.shaxian.biz.api.PageResult;
import com.shaxian.biz.appservice.tenant.TenantAppService;
import com.shaxian.biz.dto.tenant.request.CreateTenantRequest;
import com.shaxian.biz.entity.Tenant;
import com.shaxian.biz.entity.User;
import com.shaxian.biz.entity.UserTenant;
import com.shaxian.biz.repository.TenantRepository;
import com.shaxian.biz.repository.UserRepository;
import com.shaxian.biz.service.tenant.TenantService;
import com.shaxian.biz.service.user.UserTenantService;
import com.shaxian.crm.dto.request.CreateCrmSalesOrderRequest;
import com.shaxian.crm.dto.request.CrmSalesOrderQueryRequest;
import com.shaxian.crm.dto.request.UpdateCrmSalesOrderRequest;
import com.shaxian.crm.entity.CrmCustomer;
import com.shaxian.crm.entity.CrmSalesOrder;
import com.shaxian.crm.entity.CrmSalesOrderItem;
import com.shaxian.crm.repository.CrmCustomerRepository;
import com.shaxian.crm.service.CrmCustomerService;
import com.shaxian.crm.service.CrmSalesService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CrmSalesAppService {

    private final CrmSalesService crmSalesService;
    private final CrmCustomerService crmCustomerService;
    private final CrmCustomerRepository crmCustomerRepository;
    private final TenantRepository tenantRepository;
    private final TenantAppService tenantAppService;
    private final TenantService tenantService;
    private final UserRepository userRepository;
    private final UserTenantService userTenantService;

    @Value("${tenant.default-expiry-days:7}")
    private int defaultExpiryDays;

    public CrmSalesAppService(
            CrmSalesService crmSalesService,
            CrmCustomerService crmCustomerService,
            CrmCustomerRepository crmCustomerRepository,
            TenantRepository tenantRepository,
            TenantAppService tenantAppService,
            TenantService tenantService,
            UserRepository userRepository,
            UserTenantService userTenantService) {
        this.crmSalesService = crmSalesService;
        this.crmCustomerService = crmCustomerService;
        this.crmCustomerRepository = crmCustomerRepository;
        this.tenantRepository = tenantRepository;
        this.tenantAppService = tenantAppService;
        this.tenantService = tenantService;
        this.userRepository = userRepository;
        this.userTenantService = userTenantService;
    }

    @Transactional
    public CrmSalesOrder createSalesOrder(CreateCrmSalesOrderRequest request) {
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
        // 获取现有订单，用于状态比较和获取客户ID
        CrmSalesOrder existingOrder = crmSalesService.getSalesById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售单不存在"));
        
        CrmSalesOrder.OrderStatus oldStatus = existingOrder.getStatus();
        CrmSalesOrder.OrderStatus newStatus = null;
        
        // 如果订单不是草稿状态，不允许修改订单项
        if (existingOrder.getStatus() != CrmSalesOrder.OrderStatus.DRAFT) {
            // 非草稿状态下，只允许修改备注、操作员等非关键字段
            // 不允许修改客户名称、销售日期、订单项等
            if (request.getCustomerName() != null || request.getSalesDate() != null) {
                throw new IllegalArgumentException("付款后不允许修改客户名称和销售日期");
            }
            // 如果传入了订单项，检查是否与现有订单项一致（实际上不应该传入）
            if (request.getItems() != null && !request.getItems().isEmpty()) {
                throw new IllegalArgumentException("付款后不允许修改订单项");
            }
        }
        
        // 构建订单实体
        CrmSalesOrder order = new CrmSalesOrder();
        if (request.getCustomerName() != null) order.setCustomerName(request.getCustomerName());
        if (request.getSalesDate() != null) order.setSalesDate(request.getSalesDate());
        if (request.getPaidAmount() != null) order.setPaidAmount(request.getPaidAmount());
        if (request.getOperator() != null) order.setOperator(request.getOperator());
        if (request.getRemark() != null) order.setRemark(request.getRemark());
        if (request.getStatus() != null) {
            try {
                newStatus = CrmSalesOrder.OrderStatus.valueOf(request.getStatus());
                order.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("无效的状态值: " + request.getStatus());
            }
        }

        // 构建订单项（非草稿状态下，使用现有订单项）
        List<CrmSalesOrderItem> items;
        if (existingOrder.getStatus() != CrmSalesOrder.OrderStatus.DRAFT) {
            // 非草稿状态，使用现有订单项
            items = existingOrder.getItems() != null ? existingOrder.getItems() : List.of();
        } else {
            // 草稿状态，使用请求中的订单项
            items = request.getItems().stream().map(itemRequest -> {
                CrmSalesOrderItem item = new CrmSalesOrderItem();
                item.setProductId(itemRequest.getProductId());
                item.setProductName(itemRequest.getProductName());
                item.setProductCode(itemRequest.getProductCode());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setQuantity(itemRequest.getQuantity());
                item.setRemark(itemRequest.getRemark());
                return item;
            }).toList();
        }

        // 更新订单
        CrmSalesOrder updatedOrder = crmSalesService.updateSales(id, order, items);
        
        // 检查订单状态是否更新为 REVIEWED（从其他状态变为 REVIEWED）
        if (newStatus != null && newStatus == CrmSalesOrder.OrderStatus.REVIEWED 
                && oldStatus != CrmSalesOrder.OrderStatus.REVIEWED) {
            // 检查该客户是否已有租户
            Long crmCustomerId = existingOrder.getCrmCustomerId();
            Optional<Tenant> existingTenant = tenantRepository.findByCrmCustomerId(crmCustomerId);
            
            // 获取CRM客户信息
            CrmCustomer customer = crmCustomerRepository.findById(crmCustomerId)
                    .orElseThrow(() -> new IllegalArgumentException("CRM客户不存在"));
            
            Tenant tenant;
            if (existingTenant.isEmpty()) {
                // 创建租户
                CreateTenantRequest tenantRequest = new CreateTenantRequest();
                tenantRequest.setName(customer.getName());
                tenantRequest.setAddress(customer.getAddress() != null ? customer.getAddress() : "");
                
                // 创建租户，传入crmCustomerId，不关联用户（userId传null）
                tenant = tenantAppService.createTenant(tenantRequest, null, crmCustomerId);
                
                // 更新客户类型为正式客户
                crmCustomerService.updateCustomerType(crmCustomerId, CrmCustomer.CustomerType.OFFICIAL);
            } else {
                tenant = existingTenant.get();
            }
            
            // 根据CRM客户的手机号查找或创建用户，并将用户与租户关联
            if (customer.getPhone() != null && !customer.getPhone().trim().isEmpty()) {
                User user = userRepository.findByPhone(customer.getPhone()).orElse(null);
                
                if (user == null) {
                    // 如果用户不存在，自动创建用户
                    user = new User();
                    user.setPhone(customer.getPhone());
                    user.setName(customer.getName());
                    user.setPassword("123456"); // 默认密码
                    user.setStatus(User.UserStatus.ACTIVE);
                    user = userRepository.save(user);
                }
                
                // 检查是否已关联租户，如果没有则创建关联
                if (!userTenantService.isUserAssociatedWithTenant(user.getId(), tenant.getId())) {
                    // 关联用户与租户，设置为默认租户（因为这是通过销售订单复核创建的正式租户）
                    userTenantService.associateUserWithTenant(
                            user.getId(), 
                            tenant.getId(), 
                            UserTenant.RelationshipType.MEMBER, 
                            true  // 设置为默认租户
                    );
                }
            }
        }
        
        return updatedOrder;
    }

    @Transactional
    public void deleteSalesOrder(Long id) {
        crmSalesService.deleteSales(id);
    }

    @Transactional
    public CrmSalesOrder paySalesOrder(Long id) {
        return crmSalesService.paySales(id);
    }

    @Transactional
    public CrmSalesOrder reviewSalesOrder(Long id) {
        // 获取订单信息
        CrmSalesOrder order = crmSalesService.getSalesById(id)
                .orElseThrow(() -> new IllegalArgumentException("销售单不存在"));
        
        // 复核订单（更新状态为REVIEWED）
        CrmSalesOrder reviewedOrder = crmSalesService.reviewSales(id);
        
        // 处理租户逻辑
        Long crmCustomerId = order.getCrmCustomerId();
        Optional<Tenant> existingTenant = tenantRepository.findByCrmCustomerId(crmCustomerId);
        
        // 获取CRM客户信息
        CrmCustomer customer = crmCustomerRepository.findById(crmCustomerId)
                .orElseThrow(() -> new IllegalArgumentException("CRM客户不存在"));
        
        Tenant tenant;
        if (existingTenant.isEmpty()) {
            // 如果没有租户：创建租户并更新客户类型为OFFICIAL
            CreateTenantRequest tenantRequest = new CreateTenantRequest();
            tenantRequest.setName(customer.getName());
            tenantRequest.setAddress(customer.getAddress() != null ? customer.getAddress() : "");
            
            // 创建租户，传入crmCustomerId，不关联用户（userId传null）
            tenant = tenantAppService.createTenant(tenantRequest, null, crmCustomerId);
            
            // 更新客户类型为正式客户
            crmCustomerService.updateCustomerType(crmCustomerId, CrmCustomer.CustomerType.OFFICIAL);
        } else {
            // 如果已有租户：更新租户信息（延长有效期，确保状态为ACTIVE）
            tenant = existingTenant.get();
            
            // 延长有效期（在当前有效期基础上再延长，如果已过期则从当前时间开始计算）
            LocalDateTime newExpiresAt;
            if (tenant.getExpiresAt() != null && tenant.getExpiresAt().isAfter(LocalDateTime.now())) {
                // 如果还未过期，在当前有效期基础上延长
                newExpiresAt = tenant.getExpiresAt().plusDays(defaultExpiryDays);
            } else {
                // 如果已过期，从当前时间开始计算
                newExpiresAt = LocalDateTime.now().plusDays(defaultExpiryDays);
            }
            tenant.setExpiresAt(newExpiresAt);
            
            // 确保状态为ACTIVE
            tenant.setStatus(Tenant.TenantStatus.ACTIVE);
            
            // 更新租户
            tenant = tenantService.update(tenant.getId(), tenant);
        }
        
        // 根据CRM客户的手机号查找或创建用户，并将用户与租户关联
        if (customer.getPhone() != null && !customer.getPhone().trim().isEmpty()) {
            User user = userRepository.findByPhone(customer.getPhone()).orElse(null);
            
            if (user == null) {
                // 如果用户不存在，自动创建用户
                user = new User();
                user.setPhone(customer.getPhone());
                user.setName(customer.getName());
                user.setPassword("123456"); // 默认密码
                user.setStatus(User.UserStatus.ACTIVE);
                user = userRepository.save(user);
            }
            
            // 检查是否已关联租户，如果没有则创建关联
            if (!userTenantService.isUserAssociatedWithTenant(user.getId(), tenant.getId())) {
                // 关联用户与租户，设置为默认租户（因为这是通过销售订单复核创建的正式租户）
                userTenantService.associateUserWithTenant(
                        user.getId(), 
                        tenant.getId(), 
                        UserTenant.RelationshipType.MEMBER, 
                        true  // 设置为默认租户
                );
            }
        }
        
        return reviewedOrder;
    }
}

