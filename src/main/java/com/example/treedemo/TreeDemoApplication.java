package com.example.treedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.treedemo.repository")
@EntityScan(basePackages = "com.example.treedemo.entity")
@EnableTransactionManagement
public class TreeDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(TreeDemoApplication.class, args);
    }

}
