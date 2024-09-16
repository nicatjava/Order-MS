package com.example.orderms.feign;

import com.example.orderms.dto.ProductResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-ms", url = "http://localhost:8081/api/v1/products")
public interface ProductClient {

    @GetMapping("/{productId}")
    ProductResponseDto findById(@PathVariable("productId") Long productId);

    @GetMapping("/{productId}/price")
    Double getProductPrice(@PathVariable("productId") Long productId);

    @GetMapping("/{productId}/count")
    Integer getProductCount(@PathVariable("productId") Long productId);

    @PutMapping("/{productId}/count")
    void updateProductCount(@PathVariable("productId") Long productId, @RequestParam Integer newCount);
}
