package com.andy.warehouse.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {
    
    String[] value() default {};
    
    boolean requireAll() default false;
}
