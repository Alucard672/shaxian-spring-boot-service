package com.shaxian.service.user;

import com.shaxian.entity.Employee;
import com.shaxian.entity.Tenant;
import com.shaxian.entity.User;
import com.shaxian.entity.UserTenant;
import com.shaxian.repository.EmployeeRepository;
import com.shaxian.repository.TenantRepository;
import com.shaxian.repository.UserRepository;
import com.shaxian.repository.UserTenantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserTenantRepository userTenantRepository;
    private final EmployeeRepository employeeRepository;
    private final TenantRepository tenantRepository;

    public UserService(UserRepository userRepository,
                       UserTenantRepository userTenantRepository,
                       EmployeeRepository employeeRepository,
                       TenantRepository tenantRepository) {
        this.userRepository = userRepository;
        this.userTenantRepository = userTenantRepository;
        this.employeeRepository = employeeRepository;
        this.tenantRepository = tenantRepository;
    }

    public Optional<User> getByPhone(String phone) {
        return userRepository.findByPhone(phone);
    }

    @Transactional
    public User create(User user) {
        if (userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("手机号已存在");
        }
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User user) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        if (!existing.getPhone().equals(user.getPhone()) &&
                userRepository.existsByPhone(user.getPhone())) {
            throw new IllegalArgumentException("手机号已存在");
        }

        user.setId(id);
        user.setCreatedAt(existing.getCreatedAt());
        return userRepository.save(user);
    }

    public List<UserTenant> getUserTenants(Long userId) {
        return userTenantRepository.findByUserId(userId);
    }

    public Optional<Tenant> getDefaultTenant(Long userId) {
        Optional<UserTenant> userTenant = userTenantRepository.findByUserIdAndIsDefaultTrue(userId);
        if (userTenant.isEmpty()) {
            return Optional.empty();
        }
        return tenantRepository.findById(userTenant.get().getTenantId());
    }

    /**
     * 查找并设置默认租户
     * 优先级：1. 已有默认租户 2. 用户自己创建的租户(OWNER) 3. 最新绑定的租户
     */
    @Transactional
    public Optional<Tenant> findAndSetDefaultTenant(Long userId) {
        // 1. 先查找是否已有默认租户
        Optional<UserTenant> defaultTenant = userTenantRepository.findByUserIdAndIsDefaultTrue(userId);
        if (defaultTenant.isPresent()) {
            return tenantRepository.findById(defaultTenant.get().getTenantId());
        }

        // 2. 如果没有默认租户，查找用户自己创建的租户(OWNER)
        List<UserTenant> ownerTenants = userTenantRepository
                .findByUserIdAndRelationshipTypeOrderByCreatedAtDesc(userId, UserTenant.RelationshipType.OWNER);
        if (!ownerTenants.isEmpty()) {
            UserTenant ownerTenant = ownerTenants.get(0); // 取最新的
            ownerTenant.setIsDefault(true);
            userTenantRepository.save(ownerTenant);
            return tenantRepository.findById(ownerTenant.getTenantId());
        }

        // 3. 如果还是没有，查找最新绑定的可访问租户
        List<UserTenant> allTenants = userTenantRepository.findByUserIdOrderByCreatedAtDesc(userId);
        if (!allTenants.isEmpty()) {
            UserTenant latestTenant = allTenants.get(0); // 取最新的
            latestTenant.setIsDefault(true);
            userTenantRepository.save(latestTenant);
            return tenantRepository.findById(latestTenant.getTenantId());
        }

        return Optional.empty();
    }

    @Transactional
    public void setDefaultTenant(Long userId, Long tenantId) {
        // 验证用户和租户关联是否存在
        Optional<UserTenant> userTenant = userTenantRepository.findByUserIdAndTenantId(userId, tenantId);
        if (userTenant.isEmpty()) {
            throw new IllegalArgumentException("用户未关联该租户");
        }

        // 取消当前默认租户
        Optional<UserTenant> currentDefault = userTenantRepository.findByUserIdAndIsDefaultTrue(userId);
        if (currentDefault.isPresent() && !currentDefault.get().getId().equals(userTenant.get().getId())) {
            currentDefault.get().setIsDefault(false);
            userTenantRepository.save(currentDefault.get());
        }

        // 设置新的默认租户
        userTenant.get().setIsDefault(true);
        userTenantRepository.save(userTenant.get());
    }

    /**
     * 授权员工登录
     * 将员工数据同步到用户表，并建立租户关联
     */
    @Transactional
    public User authorizeEmployeeLogin(Long employeeId, Long tenantId) {
        // 1. 查找Employee
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("员工不存在"));

        if (employee.getPhone() == null || employee.getPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("员工手机号不能为空");
        }

        // 验证租户存在
        tenantRepository.findById(tenantId)
                .orElseThrow(() -> new IllegalArgumentException("租户不存在"));

        // 2. 检查User是否存在
        Optional<User> existingUser = userRepository.findByPhone(employee.getPhone());

        if (existingUser.isPresent()) {
            // 情况A：User已存在
            User user = existingUser.get();

            // 检查关联是否已存在
            Optional<UserTenant> existingRelation = userTenantRepository
                    .findByUserIdAndTenantId(user.getId(), tenantId);

            if (existingRelation.isEmpty()) {
                // 创建关联
                UserTenant userTenant = new UserTenant();
                userTenant.setUserId(user.getId());
                userTenant.setTenantId(tenantId);

                // 如果用户还没有默认租户，设为默认
                Optional<UserTenant> defaultTenant = userTenantRepository
                        .findByUserIdAndIsDefaultTrue(user.getId());
                userTenant.setIsDefault(defaultTenant.isEmpty());

                userTenantRepository.save(userTenant);
            }

            return user;
        } else {
            // 情况B：User不存在，创建User和关联
            User user = new User();
            user.setPhone(employee.getPhone());
            user.setName(employee.getName());
            user.setEmail(employee.getEmail());
            user.setPassword(employee.getPassword() != null && !employee.getPassword().isEmpty()
                    ? employee.getPassword() : "123456");
            user.setEmployeeId(employee.getId());
            user.setStatus(User.UserStatus.ACTIVE);
            user = userRepository.save(user);

            // 创建关联（第一个租户，设为默认）
            UserTenant userTenant = new UserTenant();
            userTenant.setUserId(user.getId());
            userTenant.setTenantId(tenantId);
            userTenant.setIsDefault(true);
            userTenantRepository.save(userTenant);

            return user;
        }
    }
}
