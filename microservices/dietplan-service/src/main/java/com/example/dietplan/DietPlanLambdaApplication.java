package com.example.dietplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class DietPlanLambdaApplication {
    public static void main(String[] args) {
        SpringApplication.run(DietPlanLambdaApplication.class, args);
    }
}