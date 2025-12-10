package com.obs.inventory.service.impl;

import com.obs.inventory.dto.OrderRequestDto;
import com.obs.inventory.dto.OrderResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.OrderSearchDto;
import com.obs.inventory.entity.ItemEntity;
import com.obs.inventory.entity.OrderEntity;
import com.obs.inventory.exception.ErrorBusinessException;
import com.obs.inventory.repository.ItemRepository;
import com.obs.inventory.repository.OrderRepository;
import com.obs.inventory.service.OrderService;
import com.obs.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final OrderRepository orderRepository;

    private final ItemRepository itemRepository;

    private final StockService stockService;

    @Override
    public Page<OrderResponseDto> getOrdersPages(OrderSearchDto orderSearchDto, Pageable pageable) {
        Specification<OrderEntity> spec = (root, query, cb) -> cb.conjunction();

        if (StringUtils.hasText(orderSearchDto.getOrderNo())) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("orderNo"), orderSearchDto.getOrderNo()));
        }

        if (orderSearchDto.getItemId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("item").get("id"), orderSearchDto.getItemId()));
        }

        return orderRepository.findAll(spec, pageable)
                .map(this::toDto);
    }

    @Override
    public ResponseMessage<OrderResponseDto> getOrder(String orderNo) {

        OrderEntity orderEntity = orderRepository.findById(orderNo)
                .orElseThrow(() -> new ErrorBusinessException("Order not found", "ORD-404"));

        return buildResponse(Collections.singletonList(toDto(orderEntity)), false, null, "Success get order");
    }

    @Override
    public ResponseMessage<OrderResponseDto> createOrder(OrderRequestDto request) {

        validateOrderRequest(request);

        if (orderRepository.existsById(request.getOrderNo())) {
            throw new ErrorBusinessException("Order number already exists", "ORD-002");
        }

        ItemEntity item = itemRepository.findById(String.valueOf(request.getItemId()))
                .orElseThrow(() -> new ErrorBusinessException("Item not found", "ORD-ITEM-404"));

        long remaining = stockService.getRemainingStock(request.getItemId());
        if (remaining < request.getQty()) {
            throw new ErrorBusinessException("Insufficient stock", "ORD-001");
        }

        OrderEntity orderEntity = OrderEntity.builder()
                .orderNo(request.getOrderNo().trim())
                .item(item)
                .qty(request.getQty())
                .price(item.getPrice())
                .build();

        orderEntity = orderRepository.save(orderEntity);

        return buildResponse(Collections.singletonList(toDto(orderEntity)), false, null, "Order created successfully");
    }

    @Override
    public ResponseMessage<OrderResponseDto> updateOrder(OrderRequestDto request) {
        validateOrderRequest(request);

        OrderEntity entity = orderRepository.findById(request.getOrderNo())
                .orElseThrow(() -> new ErrorBusinessException("Order no not found", "ORD-404"));

        ItemEntity item = itemRepository.findById(String.valueOf(request.getItemId()))
                .orElseThrow(() -> new ErrorBusinessException("Item not found", "ORD-ITEM-404"));

        long remaining = stockService.getRemainingStock(request.getItemId());
        if (remaining + entity.getQty() < request.getQty()) {
            throw new ErrorBusinessException("Insufficient stock", "ORD-001");
        }

        entity.setItem(item);
        entity.setQty(request.getQty());
        entity.setPrice(item.getPrice());

        entity = orderRepository.save(entity);

        return buildResponse(Collections.singletonList(toDto(entity)), false, null,
                "Order updated successfully");
    }

    @Override
    public ResponseMessage<Object> deleteOrder(String orderNo) {

        OrderEntity orderEntity = orderRepository.findById(orderNo)
                .orElseThrow(() -> new ErrorBusinessException("Order not found", "ORD-404"));

        orderRepository.delete(orderEntity);

        return buildResponse(Collections.emptyList(), false, null, "Order delete successfully");
    }

    // ============ HELPER ============

    private OrderResponseDto toDto(OrderEntity entity) {
        return OrderResponseDto.builder()
                .orderNo(entity.getOrderNo())
                .itemId(entity.getItem().getId())
                .qty(entity.getQty())
                .price(entity.getPrice())
                .build();
    }

    private <T> ResponseMessage<T> buildResponse(List<T> data,
                                                 boolean isError,
                                                 String errorNumber,
                                                 String message) {
        ResponseMessage<T> resp = new ResponseMessage<>();
        resp.setIsError(isError);
        resp.setErrorNumber(errorNumber);
        resp.setMessage(message);
        resp.setTrxDateResponse(LocalDateTime.now().format(FORMATTER));
        resp.setData(data);
        return resp;
    }

    private void validateOrderRequest(OrderRequestDto request) {
        if (request == null) {
            throw new ErrorBusinessException("Request body cannot be null", "ORD-VAL-000");
        }
        if (request.getOrderNo() == null || request.getOrderNo().trim().isEmpty()) {
            throw new ErrorBusinessException("Order number is required", "ORD-VAL-001");
        }
        if (request.getItemId() == null) {
            throw new ErrorBusinessException("Item ID is required", "ORD-VAL-002");
        }
        if (request.getQty() == null || request.getQty() <= 0) {
            throw new ErrorBusinessException("Quantity must be greater than 0", "ORD-VAL-003");
        }
    }

}
