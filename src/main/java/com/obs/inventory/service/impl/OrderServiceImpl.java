package com.obs.inventory.service.impl;

import com.obs.inventory.dto.OrderRequestDto;
import com.obs.inventory.dto.OrderResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.repository.OrderRepository;
import com.obs.inventory.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Page<OrderResponseDto> getOrders(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseMessage<OrderResponseDto> getOrder(String id) {
        return null;
    }

    @Override
    public ResponseMessage<OrderResponseDto> createOrder(OrderRequestDto request) {
        return null;
    }

    @Override
    public ResponseMessage<OrderResponseDto> updateOrder(String id, OrderRequestDto request) {
        return null;
    }

    @Override
    public ResponseMessage<Object> deleteOrder(String id) {
        return null;
    }
}
