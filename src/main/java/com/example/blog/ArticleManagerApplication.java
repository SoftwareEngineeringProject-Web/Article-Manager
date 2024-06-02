package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.blog.repository")
@EntityScan(basePackages = "com.example.blog.entity")

public class ArticleManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ArticleManagerApplication.class, args);
    }
}
