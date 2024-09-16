package com.example.orderms.controller;

import com.example.orderms.dto.OrderRequestDto;
import com.example.orderms.dto.OrderResponseDto;
import com.example.orderms.dto.PinRequestDto;
import com.example.orderms.service.OrderService;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponseDto> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/{orderId}")
    public OrderResponseDto getOrderById(@PathVariable("orderId") Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("customer/{customerId}")
    public List<OrderResponseDto> getOrdersByCustomerId(@PathVariable("customerId") Long customerId) {
        return orderService.getOrdersByCustomerId(customerId);
    }

    @PostMapping
    public OrderResponseDto createOrder(@RequestBody OrderRequestDto orderRequestDto) {
        return orderService.createOrder(orderRequestDto);
    }

    @DeleteMapping("/{orderId}")
    public String deleteOrder(@PathVariable("orderId") Long orderId) {
        return orderService.deleteOrder(orderId);
    }

    @PutMapping("/{orderId}/done")
    public String doneOrder(@PathVariable("orderId") Long orderId, @RequestBody PinRequestDto pinRequestDto) {
        return orderService.doneOrder(orderId, pinRequestDto);
    }


}
