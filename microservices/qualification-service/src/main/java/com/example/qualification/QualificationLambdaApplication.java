package com.example.qualification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class QualificationLambdaApplication {
    public static void main(String[] args) {
        SpringApplication.run(QualificationLambdaApplication.class, args);
    }
}
