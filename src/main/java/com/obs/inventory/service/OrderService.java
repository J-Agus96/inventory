package com.obs.inventory.service;

import com.obs.inventory.dto.OrderRequestDto;
import com.obs.inventory.dto.OrderResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderResponseDto> getOrdersPages(Pageable pageable);

    ResponseMessage<OrderResponseDto> getOrder(String orderNo);

    ResponseMessage<OrderResponseDto> createOrder(OrderRequestDto request);

    ResponseMessage<OrderResponseDto> updateOrder(OrderRequestDto request);

    ResponseMessage<Object> deleteOrder(String orderNo);

}
