package com.example.deliveryappproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DeliveryAppProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeliveryAppProjectApplication.class, args);
    }

}
