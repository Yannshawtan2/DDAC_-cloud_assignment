package com.example.treatmentplan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TreatmentPlanLambdaApplication {
    public static void main(String[] args) {
        SpringApplication.run(TreatmentPlanLambdaApplication.class, args);
    }
}