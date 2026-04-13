package com.andy.warehouse.interceptor;

import com.andy.warehouse.annotation.RequirePermission;
import com.andy.warehouse.common.BusinessException;
import com.andy.warehouse.entity.Permission;
import com.andy.warehouse.security.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PermissionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RequirePermission annotation = handlerMethod.getMethodAnnotation(RequirePermission.class);
        
        if (annotation == null) {
            annotation = handlerMethod.getBeanType().getAnnotation(RequirePermission.class);
        }
        
        if (annotation == null) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("用户未登录");
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof SecurityUser)) {
            throw new BusinessException("用户信息异常");
        }

        SecurityUser securityUser = (SecurityUser) principal;
        Set<String> userPermissions = securityUser.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toSet());

        String[] requiredPermissions = annotation.value();
        if (requiredPermissions.length == 0) {
            return true;
        }

        boolean requireAll = annotation.requireAll();
        if (requireAll) {
            boolean hasAllPermissions = Arrays.stream(requiredPermissions)
                    .allMatch(userPermissions::contains);
            if (!hasAllPermissions) {
                throw new BusinessException("权限不足，需要权限: " + String.join(", ", requiredPermissions));
            }
        } else {
            boolean hasAnyPermission = Arrays.stream(requiredPermissions)
                    .anyMatch(userPermissions::contains);
            if (!hasAnyPermission) {
                throw new BusinessException("权限不足，需要以下任一权限: " + String.join(", ", requiredPermissions));
            }
        }

        return true;
    }
}
