package com.example.orderms.service;

import com.example.orderms.dto.OrderRequestDto;
import com.example.orderms.dto.OrderResponseDto;
import com.example.orderms.dto.PinRequestDto;

import java.util.List;

public interface OrderService {
    List<OrderResponseDto> getAllOrders();
    OrderResponseDto getOrderById(long orderId);
    List<OrderResponseDto> getOrdersByCustomerId(Long customerId);
    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);
    String deleteOrder(Long orderId);

    String doneOrder(Long orderId, PinRequestDto pin);

}
