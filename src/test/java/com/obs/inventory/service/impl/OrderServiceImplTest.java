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
import com.obs.inventory.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private OrderServiceImpl orderService;

    // ===== helper =====

    private ItemEntity buildItem(Integer id, String name, BigDecimal price) {
        return ItemEntity.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
    }

    private OrderEntity buildOrder(String orderNo, ItemEntity item, Long qty, BigDecimal price) {
        return OrderEntity.builder()
                .orderNo(orderNo)
                .item(item)
                .qty(qty)
                .price(price)
                .build();
    }

    private OrderRequestDto buildRequest(String orderNo, Integer itemId, Long qty) {
        OrderRequestDto dto = new OrderRequestDto();
        dto.setOrderNo(orderNo);
        dto.setItemId(itemId);
        dto.setQty(qty);
        return dto;
    }

    // ===== getOrdersPages =====

    @Test
    @DisplayName("getOrdersPages - tanpa filter (hanya pageable)")
    void getOrdersPages_noFilter() {
        OrderSearchDto search = new OrderSearchDto(); // orderNo & itemId null
        Pageable pageable = PageRequest.of(0, 10, Sort.by("orderNo"));

        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        OrderEntity entity = buildOrder("O1", item, 2L, BigDecimal.valueOf(5));

        when(orderRepository.findAll(org.mockito.Mockito.<Specification<OrderEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        Page<OrderResponseDto> page = orderService.getOrdersPages(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        OrderResponseDto dto = page.getContent().get(0);
        assertThat(dto.getOrderNo()).isEqualTo("O1");
        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getQty()).isEqualTo(2);
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(5));

        verify(orderRepository).findAll(org.mockito.Mockito.<Specification<OrderEntity>>any(), eq(pageable));
    }

    @Test
    @DisplayName("getOrdersPages - filter hanya orderNo")
    void getOrdersPages_filterByOrderNo() {
        OrderSearchDto search = new OrderSearchDto();
        search.setOrderNo("O2");
        Pageable pageable = PageRequest.of(0, 10);

        ItemEntity item = buildItem(2, "Book", BigDecimal.TEN);
        OrderEntity entity = buildOrder("O2", item, 3L, BigDecimal.TEN);

        when(orderRepository.findAll(org.mockito.Mockito.<Specification<OrderEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        Page<OrderResponseDto> page = orderService.getOrdersPages(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getOrderNo()).isEqualTo("O2");
    }

    @Test
    @DisplayName("getOrdersPages - filter hanya itemId")
    void getOrdersPages_filterByItemId() {
        OrderSearchDto search = new OrderSearchDto();
        search.setItemId(3);
        Pageable pageable = PageRequest.of(0, 10);

        ItemEntity item = buildItem(3, "Bag", BigDecimal.valueOf(30));
        OrderEntity entity = buildOrder("O3", item, 4L, BigDecimal.valueOf(30));

        when(orderRepository.findAll(org.mockito.Mockito.<Specification<OrderEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        Page<OrderResponseDto> page = orderService.getOrdersPages(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getItemId()).isEqualTo(3);
    }

    @Test
    @DisplayName("getOrdersPages - filter orderNo dan itemId sekaligus")
    void getOrdersPages_filterOrderNoAndItemId() {
        OrderSearchDto search = new OrderSearchDto();
        search.setOrderNo("O4");
        search.setItemId(4);

        Pageable pageable = PageRequest.of(0, 10);
        ItemEntity item = buildItem(4, "Pencil", BigDecimal.valueOf(3));
        OrderEntity entity = buildOrder("O4", item, 1L, BigDecimal.valueOf(3));

        when(orderRepository.findAll(org.mockito.Mockito.<Specification<OrderEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        Page<OrderResponseDto> page = orderService.getOrdersPages(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        OrderResponseDto dto = page.getContent().get(0);
        assertThat(dto.getOrderNo()).isEqualTo("O4");
        assertThat(dto.getItemId()).isEqualTo(4);
    }

    // ===== getOrder =====

    @Test
    @DisplayName("getOrder - sukses")
    void getOrder_success() {
        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        OrderEntity entity = buildOrder("O1", item, 2L, BigDecimal.valueOf(5));

        when(orderRepository.findById("O1")).thenReturn(Optional.of(entity));

        ResponseMessage<OrderResponseDto> resp = orderService.getOrder("O1");

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Success get order");
        assertThat(resp.getData()).hasSize(1);
        OrderResponseDto dto = resp.getData().get(0);
        assertThat(dto.getOrderNo()).isEqualTo("O1");
        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getQty()).isEqualTo(2);
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(5));
    }

    @Test
    @DisplayName("getOrder - order tidak ditemukan -> ORD-404")
    void getOrder_notFound() {
        when(orderRepository.findById("OX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrder("OX"))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Order not found")
                .extracting("errorNumber")
                .isEqualTo("ORD-404");
    }

    // ===== createOrder =====

    @Test
    @DisplayName("createOrder - sukses")
    void createOrder_success() {
        OrderRequestDto req = buildRequest("O1", 1, 2L);

        when(orderRepository.existsById("O1")).thenReturn(false);

        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        when(itemRepository.findById("1")).thenReturn(Optional.of(item));

        when(stockService.getRemainingStock(1)).thenReturn(10L);

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseMessage<OrderResponseDto> resp = orderService.createOrder(req);

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Order created successfully");
        assertThat(resp.getData()).hasSize(1);

        OrderResponseDto dto = resp.getData().get(0);
        assertThat(dto.getOrderNo()).isEqualTo("O1");
        assertThat(dto.getItemId()).isEqualTo(1);
        assertThat(dto.getQty()).isEqualTo(2);
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(5));

        ArgumentCaptor<OrderEntity> captor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(captor.capture());
        assertThat(captor.getValue().getOrderNo()).isEqualTo("O1");
        assertThat(captor.getValue().getItem().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("createOrder - orderNo sudah ada -> ORD-002")
    void createOrder_orderNoAlreadyExists() {
        OrderRequestDto req = buildRequest("O1", 1, 2L);
        when(orderRepository.existsById("O1")).thenReturn(true);

        assertThatThrownBy(() -> orderService.createOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Order number already exists")
                .extracting("errorNumber")
                .isEqualTo("ORD-002");
    }

    @Test
    @DisplayName("createOrder - item tidak ditemukan -> ORD-ITEM-404")
    void createOrder_itemNotFound() {
        OrderRequestDto req = buildRequest("O1", 1, 2L);
        when(orderRepository.existsById("O1")).thenReturn(false);
        when(itemRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.createOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item not found")
                .extracting("errorNumber")
                .isEqualTo("ORD-ITEM-404");
    }

    @Test
    @DisplayName("createOrder - stock tidak cukup -> ORD-001")
    void createOrder_insufficientStock() {
        OrderRequestDto req = buildRequest("O1", 1, 5L);
        when(orderRepository.existsById("O1")).thenReturn(false);

        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        when(itemRepository.findById("1")).thenReturn(Optional.of(item));
        when(stockService.getRemainingStock(1)).thenReturn(2L); // < qty

        assertThatThrownBy(() -> orderService.createOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Insufficient stock")
                .extracting("errorNumber")
                .isEqualTo("ORD-001");
    }

    // ===== updateOrder =====

    @Test
    @DisplayName("updateOrder - sukses")
    void updateOrder_success() {
        OrderRequestDto req = buildRequest("O1", 2, 3L);

        ItemEntity oldItem = buildItem(1, "Pen", BigDecimal.valueOf(5));
        OrderEntity existing = buildOrder("O1", oldItem, 1L, BigDecimal.valueOf(5));

        ItemEntity newItem = buildItem(2, "Book", BigDecimal.TEN);

        when(orderRepository.findById("O1")).thenReturn(Optional.of(existing));
        when(itemRepository.findById("2")).thenReturn(Optional.of(newItem));
        when(stockService.getRemainingStock(2)).thenReturn(5L); // 5 + oldQty(1) = 6 >= 3

        when(orderRepository.save(any(OrderEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseMessage<OrderResponseDto> resp = orderService.updateOrder(req);

        assertThat(resp.getIsError()).isFalse();
        OrderResponseDto dto = resp.getData().get(0);
        assertThat(dto.getOrderNo()).isEqualTo("O1");
        assertThat(dto.getItemId()).isEqualTo(2);
        assertThat(dto.getQty()).isEqualTo(3);
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.TEN);
    }

    @Test
    @DisplayName("updateOrder - order tidak ditemukan -> ORD-404")
    void updateOrder_orderNotFound() {
        OrderRequestDto req = buildRequest("OX", 1, 2L);
        when(orderRepository.findById("OX")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Order no not found")
                .extracting("errorNumber")
                .isEqualTo("ORD-404");
    }

    @Test
    @DisplayName("updateOrder - item tidak ditemukan -> ORD-ITEM-404")
    void updateOrder_itemNotFound() {
        OrderRequestDto req = buildRequest("O1", 1, 2L);

        ItemEntity oldItem = buildItem(9, "Old", BigDecimal.ONE);
        OrderEntity existing = buildOrder("O1", oldItem, 1L, BigDecimal.ONE);

        when(orderRepository.findById("O1")).thenReturn(Optional.of(existing));
        when(itemRepository.findById("1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.updateOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item not found")
                .extracting("errorNumber")
                .isEqualTo("ORD-ITEM-404");
    }

    @Test
    @DisplayName("updateOrder - stock tidak cukup -> ORD-001")
    void updateOrder_insufficientStock() {
        OrderRequestDto req = buildRequest("O1", 1, 10L);

        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        OrderEntity existing = buildOrder("O1", item, 5L, BigDecimal.valueOf(5));

        when(orderRepository.findById("O1")).thenReturn(Optional.of(existing));
        when(itemRepository.findById("1")).thenReturn(Optional.of(item));
        when(stockService.getRemainingStock(1)).thenReturn(1L); // 1 + 5 < 10

        assertThatThrownBy(() -> orderService.updateOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Insufficient stock")
                .extracting("errorNumber")
                .isEqualTo("ORD-001");
    }

    // ===== deleteOrder =====

    @Test
    @DisplayName("deleteOrder - sukses")
    void deleteOrder_success() {
        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        OrderEntity existing = buildOrder("O1", item, 2L, BigDecimal.valueOf(5));

        when(orderRepository.findById("O1")).thenReturn(Optional.of(existing));

        ResponseMessage<Object> resp = orderService.deleteOrder("O1");

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Order delete successfully");
        verify(orderRepository).delete(existing);
    }

    // ===== validateOrderRequest (via createOrder) =====

    @Test
    @DisplayName("validateOrderRequest - request null -> ORD-VAL-000")
    void createOrder_requestNull() {
        assertThatThrownBy(() -> orderService.createOrder(null))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Request body cannot be null")
                .extracting("errorNumber")
                .isEqualTo("ORD-VAL-000");
    }

    @Test
    @DisplayName("validateOrderRequest - orderNo kosong -> ORD-VAL-001")
    void createOrder_orderNoBlank() {
        OrderRequestDto req = buildRequest("   ", 1, 2L);

        assertThatThrownBy(() -> orderService.createOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Order number is required")
                .extracting("errorNumber")
                .isEqualTo("ORD-VAL-001");
    }

    @Test
    @DisplayName("validateOrderRequest - itemId null -> ORD-VAL-002")
    void createOrder_itemIdNull() {
        OrderRequestDto req = buildRequest("O1", null, 2L);

        assertThatThrownBy(() -> orderService.createOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item ID is required")
                .extracting("errorNumber")
                .isEqualTo("ORD-VAL-002");
    }

    @Test
    @DisplayName("validateOrderRequest - qty null -> ORD-VAL-003")
    void createOrder_qtyNull() {
        OrderRequestDto req = buildRequest("O1", 1, null);

        assertThatThrownBy(() -> orderService.createOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Quantity must be greater than 0")
                .extracting("errorNumber")
                .isEqualTo("ORD-VAL-003");
    }

    @Test
    @DisplayName("validateOrderRequest - qty <= 0 -> ORD-VAL-003")
    void createOrder_qtyLessOrEqualZero() {
        OrderRequestDto req = buildRequest("O1", 1, 0L);

        assertThatThrownBy(() -> orderService.createOrder(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Quantity must be greater than 0")
                .extracting("errorNumber")
                .isEqualTo("ORD-VAL-003");
    }
}
