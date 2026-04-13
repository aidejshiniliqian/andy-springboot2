package com.andy.warehouse;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@MapperScan("com.andy.warehouse.mapper")
public class WarehouseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarehouseManagementApplication.class, args);
    }
}
