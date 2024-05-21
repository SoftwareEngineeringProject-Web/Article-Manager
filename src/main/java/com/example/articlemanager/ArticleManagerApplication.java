package com.example.articlemanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class ArticleManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArticleManagerApplication.class, args);
    }

}
