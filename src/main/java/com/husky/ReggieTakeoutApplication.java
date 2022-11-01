package com.husky;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching  // 开启spring cache注解方式缓存功能
public class ReggieTakeoutApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieTakeoutApplication.class, args);
        log.info("项目启动成功");
    }

}
