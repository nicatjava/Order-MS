package com.example.orderms.feign;

import com.example.orderms.dto.CustomerResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "customer-ms", url = "http://localhost:8080/api/v1/customers")
public interface CustomerClient {

    @GetMapping("/{customerId}")
    CustomerResponseDto getCustomerById(@PathVariable("customerId") Long customerId);

    @GetMapping("/{customerId}/balance")
    Double getCustomerBalance(@PathVariable("customerId") Long customerId);

    @PutMapping("/{customerId}/balance")
    void updateCustomerBalance(@PathVariable("customerId") Long customerId, @RequestParam Double newBalance);

}