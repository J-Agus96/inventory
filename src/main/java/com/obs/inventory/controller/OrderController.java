package com.obs.inventory.controller;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.OrderRequestDto;
import com.obs.inventory.dto.OrderResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.OrderSearchDto;
import com.obs.inventory.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("")
    public Page<OrderResponseDto> getOrderPages(OrderSearchDto orderSearchDto, Pageable pageable) {
        return orderService.getOrdersPages(orderSearchDto, pageable);
    }

    @GetMapping("/{orderNo}")
    public ResponseMessage<OrderResponseDto> getOrder(@PathVariable String orderNo) {
        return orderService.getOrder(orderNo);
    }

    @PostMapping("/create")
    public ResponseMessage<OrderResponseDto> createOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        return orderService.createOrder(orderRequestDto);
    }

    @PutMapping("/update")
    public ResponseMessage<OrderResponseDto> updateOrder(@Valid @RequestBody OrderRequestDto orderRequestDto) {
        return orderService.updateOrder(orderRequestDto);
    }

    @DeleteMapping("/delete/{orderNo}")
    public ResponseMessage<Object> deleteOrder(@PathVariable String orderNo) {
        return orderService.deleteOrder(orderNo);
    }

}
