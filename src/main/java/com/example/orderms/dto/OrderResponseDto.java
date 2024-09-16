package com.example.orderms.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private Long customerId;
    private Long productId;
    private Double price;
    private Integer count;
}
