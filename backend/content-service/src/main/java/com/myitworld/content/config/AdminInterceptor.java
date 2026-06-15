package com.myitworld.content.config;

import com.myitworld.common.constant.AuthConstants;
import com.myitworld.common.exception.BusinessException;
import com.myitworld.common.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Admin 权限拦截器：校验 X-Roles 包含 ADMIN
 */
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String roles = request.getHeader(AuthConstants.HEADER_ROLES);
        if (roles == null || !roles.contains(AuthConstants.ROLE_ADMIN)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "需要管理员权限");
        }
        return true;
    }
}
