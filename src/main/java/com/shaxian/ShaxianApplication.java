package com.shaxian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(exclude = {
    RedisRepositoriesAutoConfiguration.class
})
@EntityScan(basePackages = {"com.shaxian.biz.entity", "com.shaxian.crm.entity"})
@EnableJpaRepositories(basePackages = {"com.shaxian.biz.repository", "com.shaxian.crm.repository"})
public class ShaxianApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShaxianApplication.class, args);
    }
}

