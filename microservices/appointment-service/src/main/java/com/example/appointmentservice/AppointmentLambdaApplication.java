package com.example.appointmentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.web.servlet.function.RequestPredicates.*;
import static org.springframework.web.servlet.function.RouterFunctions.route;

@SpringBootApplication
public class AppointmentLambdaApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppointmentLambdaApplication.class, args);
    }

    /**
     * Router function for AWS Lambda integration
     */
    @Bean
    public RouterFunction<ServerResponse> routes() {
        return route()
                .GET("/health", request -> ServerResponse.ok().body("Appointment Service is running"))
                .build();
    }
}