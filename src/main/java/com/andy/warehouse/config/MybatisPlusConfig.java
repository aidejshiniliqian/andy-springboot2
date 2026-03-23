package com.andy.warehouse.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
public class MyBatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                log.debug("开始插入填充...");
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());
                this.strictInsertFill(metaObject, "isDeleted", Boolean.class, false);

                Long userId = getCurrentUserId();
                if (userId != null) {
                    this.strictInsertFill(metaObject, "createdBy", Long.class, userId);
                    this.strictInsertFill(metaObject, "updatedBy", Long.class, userId);
                }
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                log.debug("开始更新填充...");
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, LocalDateTime.now());

                Long userId = getCurrentUserId();
                if (userId != null) {
                    this.strictUpdateFill(metaObject, "updatedBy", Long.class, userId);
                }
            }

            private Long getCurrentUserId() {
                try {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication != null && authentication.isAuthenticated() &&
                            !"anonymousUser".equals(authentication.getPrincipal())) {
                        Object principal = authentication.getPrincipal();
                        if (principal instanceof com.andy.warehouse.security.UserDetailsImpl) {
                            return ((com.andy.warehouse.security.UserDetailsImpl) principal).getId();
                        }
                    }
                } catch (Exception e) {
                    log.debug("获取当前用户ID失败: {}", e.getMessage());
                }
                return null;
            }
        };
    }
}
