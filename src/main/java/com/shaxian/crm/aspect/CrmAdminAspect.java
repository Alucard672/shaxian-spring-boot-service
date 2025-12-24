package com.shaxian.crm.aspect;

import com.shaxian.biz.auth.UserSession;
import com.shaxian.crm.config.CrmProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Set;

/**
 * CRM管理员权限校验切面
 * 拦截crm包下的所有Controller方法，校验当前用户是否为管理员
 */
@Aspect
@Component
public class CrmAdminAspect {

    private static final Logger logger = LoggerFactory.getLogger(CrmAdminAspect.class);
    private static final String CURRENT_USER_SESSION = "CURRENT_USER_SESSION";

    private final CrmProperties crmProperties;

    public CrmAdminAspect(CrmProperties crmProperties) {
        this.crmProperties = crmProperties;
    }

    /**
     * 拦截com.shaxian.crm.controller包下的所有方法
     */
    @Around("execution(* com.shaxian.crm.controller..*(..))")
    public Object checkAdminPermission(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取HttpServletRequest
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("无法获取请求上下文");
        }

        HttpServletRequest request = attributes.getRequest();
        
        // 从请求属性中获取UserSession（由SessionAuthInterceptor设置）
        UserSession userSession = (UserSession) request.getAttribute(CURRENT_USER_SESSION);
        if (userSession == null) {
            logger.warn("CRM接口访问失败：未找到用户会话信息");
            throw new IllegalArgumentException("未找到用户会话信息");
        }

        // 获取用户手机号
        String phone = userSession.getPhone();
        if (phone == null || phone.isEmpty()) {
            logger.warn("CRM接口访问失败：用户手机号为空, userId={}", userSession.getUserId());
            throw new IllegalArgumentException("用户手机号为空");
        }

        // 获取管理员手机号列表
        Set<String> adminPhones = crmProperties.getAdminPhones();
        if (adminPhones == null || adminPhones.isEmpty()) {
            logger.warn("CRM接口访问失败：未配置管理员手机号列表");
            throw new IllegalStateException("未配置管理员手机号列表");
        }

        // 检查用户手机号是否在管理员列表中
        if (!adminPhones.contains(phone)) {
            logger.warn("CRM接口访问被拒绝：用户手机号不在管理员列表中, phone={}, userId={}", 
                    phone, userSession.getUserId());
            throw new IllegalArgumentException("无权限访问CRM接口，仅管理员可访问");
        }

        logger.debug("CRM接口权限校验通过, phone={}, userId={}", phone, userSession.getUserId());
        
        // 权限校验通过，继续执行方法
        return joinPoint.proceed();
    }
}

