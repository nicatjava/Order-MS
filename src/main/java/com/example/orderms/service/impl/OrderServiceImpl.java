package com.example.orderms.service.impl;

import com.example.orderms.dao.entity.OrderEntity;
import com.example.orderms.dao.repository.OrderRepository;
import com.example.orderms.dto.CustomerResponseDto;
import com.example.orderms.dto.OrderRequestDto;
import com.example.orderms.dto.OrderResponseDto;
import com.example.orderms.dto.PinRequestDto;
import com.example.orderms.exception.*;
import com.example.orderms.feign.CustomerClient;
import com.example.orderms.feign.ProductClient;
import com.example.orderms.mapper.OrderMapper;
import com.example.orderms.service.OrderService;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerClient customerClient;
    private final ProductClient productClient;


    @Override
    public List<OrderResponseDto> getAllOrders() {
        log.info("Getting All Orders");
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    public OrderResponseDto getOrderById(long orderId) {
        log.info("Getting Order By Id : {}", orderId);
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));
        return orderMapper.toResponseDto(order);
    }

    @Override
    public List<OrderResponseDto> getOrdersByCustomerId(Long customerId) {
        log.info("Getting Order By Customer Id : {}", customerId);
        try {
            customerClient.getCustomerById(customerId);
        } catch (FeignException.NotFound ex) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }

        List<OrderEntity> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(orderMapper::toResponseDto)
                .toList();
    }

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {

        // Check Customer Exist
        try {
            customerClient.getCustomerById(orderRequestDto.getCustomerId());
        } catch (FeignException ex) {
            log.info("Customer not found: CustomerId={}", orderRequestDto.getCustomerId());
            throw new CustomerNotFoundException("Customer not found with id: " + orderRequestDto.getCustomerId());
        }


        // Check Product Exist
        try {
            productClient.findById(orderRequestDto.getProductId());
        } catch (FeignException ex) {
            log.info("Product not found: ProductId={}", orderRequestDto.getProductId());
            throw new ProductNotFoundException("Product not found with id: " + orderRequestDto.getProductId());
        }

        // Check Product Count
        Integer productCount = productClient.getProductCount(orderRequestDto.getProductId());
        if (productCount < orderRequestDto.getCount()) {
            log.info("Not Enough Product Count: ProductId={}, AvailableCount={}, RequestedCount={}",
                    orderRequestDto.getProductId(), productCount, orderRequestDto.getCount());
            throw new NotEnoughProductException("Not enough product");
        }

        // Check Customer Balance
        Double customerBalance = customerClient.getCustomerBalance(orderRequestDto.getCustomerId());
        Double productPrice = productClient.getProductPrice(orderRequestDto.getProductId());
        Double totalPrice = productPrice * orderRequestDto.getCount();
        if (customerBalance < totalPrice) {
            log.info("Not Enough Balance: CustomerId={}, AvailableBalance={}, TotalPriceRequired={}",
                    orderRequestDto.getCustomerId(), customerBalance, totalPrice);
            throw new NotEnoughBalanceException("Not enough balance");
        }

        // Create New Order
        log.info("Creating new order: CustomerId={}, ProductId={}, TotalPrice={}",
                orderRequestDto.getCustomerId(), orderRequestDto.getProductId(), totalPrice);
        OrderEntity order = orderMapper.toEntity(orderRequestDto);
        order.setPrice(productPrice);
        return orderMapper.toResponseDto(orderRepository.save(order));
    }

    @Override
    public String deleteOrder(Long orderId) {
        OrderEntity orderEntity = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id " + orderId));
        orderRepository.delete(orderEntity);
        log.info("Order Deleted: OrderId={}", orderId);
        return "Deleted Order Id: " + orderId;
    }

    @Override
    public String doneOrder(Long orderId, PinRequestDto pinRequestDto) {
        log.info("In Progress Order Id : {}", orderId);

        // Get Order By Id
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with id: " + orderId));

        // Verify Customer PIN
        CustomerResponseDto customer = customerClient.getCustomerById(order.getCustomerId());
        log.info("customer pin: " + customer.getPin());
        log.info("entered pin: "+pinRequestDto.getPin());
        if (customer.getPin().equals(pinRequestDto.getPin())) {

            // Check Product Count
            Integer productCount = productClient.getProductCount(order.getProductId());
            if (productCount < order.getCount()) {
                throw new NotEnoughProductException("Not enough product");
            }

            // Check Customer Balance
            Double customerBalance = customerClient.getCustomerBalance(order.getCustomerId());
            Double productPrice = productClient.getProductPrice(order.getProductId());
            Double totalPrice = productPrice * order.getCount();
            if (customerBalance < totalPrice) {
                throw new NotEnoughBalanceException("Not enough balance");
            }

            // Update Customer Balance
            customerClient.updateCustomerBalance(order.getCustomerId(), customerBalance - totalPrice);

            // Update Product Count
            productClient.updateProductCount(order.getProductId(), productCount - order.getCount());

            // Delete Order
            orderRepository.delete(order);

        } else {
            throw new InvalidPinException("Invalid pin");
        }

        log.info("Order Done: OrderId={}", orderId);
        return "Order Done";
    }

}
