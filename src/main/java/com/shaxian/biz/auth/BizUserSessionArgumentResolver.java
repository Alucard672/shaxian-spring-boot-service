package com.shaxian.biz.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * UserSession 参数解析器
 * 支持在 Controller 方法参数中直接注入当前 UserSession
 */
@Component
public class BizUserSessionArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String CURRENT_USER_SESSION = "CURRENT_USER_SESSION";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // 判断参数类型是否为 UserSession 或其子类
        return UserSession.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        if (request == null) {
            return null;
        }
        // 从请求属性中获取 UserSession（由拦截器设置）
        return request.getAttribute(CURRENT_USER_SESSION);
    }
}
