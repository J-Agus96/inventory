package com.obs.inventory.controller;

import com.obs.inventory.dto.OrderRequestDto;
import com.obs.inventory.dto.OrderResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.OrderSearchDto;
import com.obs.inventory.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    // ===== helper =====
    private OrderResponseDto buildResponseDto(String orderNo, Integer itemId, Long qty, BigDecimal price) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderNo(orderNo);
        dto.setItemId(itemId);
        dto.setQty(qty);
        dto.setPrice(price);
        return dto;
    }

    private OrderRequestDto buildRequest(String orderNo, Integer itemId, Long qty) {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setOrderNo(orderNo);
        dto.setItemId(itemId);
        dto.setQty(qty);
        return dto;
    }

    private <T> ResponseMessage<T> wrap(T data, String message) {
        ResponseMessage<T> resp = new ResponseMessage<>();
        resp.setIsError(false);
        resp.setErrorNumber(null);
        resp.setMessage(message);
        resp.setData(List.of(data));
        resp.setTrxDateResponse("dummy-date");
        return resp;
    }

    // ===== getOrderPages =====

    @Test
    @DisplayName("getOrderPages - delegasi ke service dan mengembalikan page yang sama")
    void getOrderPages_shouldDelegateToService() {
        OrderSearchDto search = new OrderSearchDto();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("orderNo"));

        OrderResponseDto dto = buildResponseDto("O1", 1, 5L, BigDecimal.valueOf(10));
        Page<OrderResponseDto> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(orderService.getOrdersPages(any(OrderSearchDto.class), any(Pageable.class)))
                .thenReturn(page);

        Page<OrderResponseDto> result =
                orderController.getOrderPages(search, pageable);

        assertThat(result).isSameAs(page);
        verify(orderService).getOrdersPages(search, pageable);
    }

    // ===== getOrder =====

    @Test
    @DisplayName("getOrder - delegasi ke service dan kembalikan response yang sama")
    void getOrder_shouldDelegateToService() {
        OrderResponseDto dto = buildResponseDto("O1", 1, 5L, BigDecimal.valueOf(10));
        ResponseMessage<OrderResponseDto> resp =
                wrap(dto, "Success get order");

        when(orderService.getOrder("O1")).thenReturn(resp);

        ResponseMessage<OrderResponseDto> result =
                orderController.getOrder("O1");

        assertThat(result).isSameAs(resp);
        verify(orderService).getOrder("O1");
    }

    // ===== createOrder =====

    @Test
    @DisplayName("createOrder - delegasi ke service")
    void createOrder_shouldDelegateToService() {
        OrderRequestDto req = buildRequest("O1", 1, 5L);
        OrderResponseDto dto = buildResponseDto("O1", 1, 5L, BigDecimal.valueOf(10));
        ResponseMessage<OrderResponseDto> resp =
                wrap(dto, "Order created successfully");

        when(orderService.createOrder(req)).thenReturn(resp);

        ResponseMessage<OrderResponseDto> result =
                orderController.createOrder(req);

        assertThat(result).isSameAs(resp);
        verify(orderService).createOrder(req);
    }

    // ===== updateOrder =====

    @Test
    @DisplayName("updateOrder - delegasi ke service")
    void updateOrder_shouldDelegateToService() {
        OrderRequestDto req = buildRequest("O1", 2, 10L);
        OrderResponseDto dto = buildResponseDto("O1", 2, 10L, BigDecimal.valueOf(15));
        ResponseMessage<OrderResponseDto> resp =
                wrap(dto, "Order updated successfully");

        when(orderService.updateOrder(req)).thenReturn(resp);

        ResponseMessage<OrderResponseDto> result =
                orderController.updateOrder(req);

        assertThat(result).isSameAs(resp);
        verify(orderService).updateOrder(req);
    }

    // ===== deleteOrder =====

    @Test
    @DisplayName("deleteOrder - delegasi ke service")
    void deleteOrder_shouldDelegateToService() {
        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(false);
        resp.setMessage("Order delete successfully");

        when(orderService.deleteOrder("O1")).thenReturn(resp);

        ResponseMessage<Object> result =
                orderController.deleteOrder("O1");

        assertThat(result).isSameAs(resp);
        verify(orderService).deleteOrder("O1");
    }
}