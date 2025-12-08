package com.obs.inventory.service;

import com.obs.inventory.dto.OrderRequestDto;
import com.obs.inventory.dto.OrderResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderResponseDto> getOrders(Pageable pageable);

    ResponseMessage<OrderResponseDto> getOrder(String id);

    ResponseMessage<OrderResponseDto> createOrder(OrderRequestDto request);

    ResponseMessage<OrderResponseDto> updateOrder(String id, OrderRequestDto request);

    ResponseMessage<Object> deleteOrder(String id);

}
