package com.example.DDac_group18;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.DDac_group18.model.repository")
public class DDacGroup18Application {
    public static void main(String[] args) {
        SpringApplication.run(DDacGroup18Application.class, args);
    }
}
