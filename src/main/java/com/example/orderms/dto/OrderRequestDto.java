package com.example.orderms.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRequestDto {
    private Long customerId;
    private Long productId;
    private Integer count;
}
