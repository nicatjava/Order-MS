package com.example.orderms.mapper;

import com.example.orderms.dao.entity.OrderEntity;
import com.example.orderms.dto.OrderRequestDto;
import com.example.orderms.dto.OrderResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponseDto toResponseDto(OrderEntity orderEntity);
    OrderEntity toEntity(OrderRequestDto orderRequestDto);
}
