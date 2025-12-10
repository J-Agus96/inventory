package com.obs.inventory.service;

import com.obs.inventory.dto.OrderRequestDto;
import com.obs.inventory.dto.OrderResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.OrderSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    Page<OrderResponseDto> getOrdersPages(OrderSearchDto orderSearchDto, Pageable pageable);

    ResponseMessage<OrderResponseDto> getOrder(String orderNo);

    ResponseMessage<OrderResponseDto> createOrder(OrderRequestDto request);

    ResponseMessage<OrderResponseDto> updateOrder(OrderRequestDto request);

    ResponseMessage<Object> deleteOrder(String orderNo);

}
