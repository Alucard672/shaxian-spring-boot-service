package com.shaxian.crm.aspect;

import com.shaxian.crm.auth.CrmUserSession;
import com.shaxian.crm.repository.CrmUserInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * CRM用户权限校验切面
 * 拦截crm包下的所有Controller方法，校验当前用户是否为CRM用户（存在于crm_user_info表）
 */
@Aspect
@Component
public class CrmAdminAspect {

    private static final Logger logger = LoggerFactory.getLogger(CrmAdminAspect.class);
    private static final String CURRENT_CRM_USER_SESSION = "CURRENT_CRM_USER_SESSION";

    private final CrmUserInfoRepository crmUserInfoRepository;

    public CrmAdminAspect(CrmUserInfoRepository crmUserInfoRepository) {
        this.crmUserInfoRepository = crmUserInfoRepository;
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
        String requestURI = request.getRequestURI();
        
        // 排除登录、登出和加载会话接口，这些接口不需要会话验证
        if (requestURI != null && (requestURI.equals("/crm/api/auth/login") 
                || requestURI.equals("/crm/api/auth/logout")
                || requestURI.equals("/crm/api/auth/session"))) {
            return joinPoint.proceed();
        }
        
        // 从请求属性中获取CrmUserSession（由CrmSessionAuthInterceptor设置）
        CrmUserSession crmUserSession = (CrmUserSession) request.getAttribute(CURRENT_CRM_USER_SESSION);
        if (crmUserSession == null) {
            logger.warn("CRM接口访问失败：未找到用户会话信息");
            throw new IllegalArgumentException("未找到用户会话信息");
        }

        // 获取用户手机号
        String phone = crmUserSession.getPhone();
        if (phone == null || phone.isEmpty()) {
            logger.warn("CRM接口访问失败：用户手机号为空, userId={}", crmUserSession.getUserId());
            throw new IllegalArgumentException("用户手机号为空");
        }

        // 检查用户是否存在于crm_user_info表中
        if (!crmUserInfoRepository.existsByPhone(phone)) {
            logger.warn("CRM接口访问被拒绝：用户不存在于CRM用户表中, phone={}, userId={}", 
                    phone, crmUserSession.getUserId());
            throw new IllegalArgumentException("无权限访问CRM接口，仅CRM用户可访问");
        }

        logger.debug("CRM接口权限校验通过, phone={}, userId={}", phone, crmUserSession.getUserId());
        
        // 权限校验通过，继续执行方法
        return joinPoint.proceed();
    }
}

