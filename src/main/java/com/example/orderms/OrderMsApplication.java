package com.example.orderms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class OrderMsApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderMsApplication.class, args);
    }

}
