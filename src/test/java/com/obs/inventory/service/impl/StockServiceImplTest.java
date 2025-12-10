package com.obs.inventory.service.impl;

import com.obs.inventory.repository.InventoryRepository;
import com.obs.inventory.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    @DisplayName("getRemainingStock - hitung stok dengan top up, withdraw, dan order")
    void getRemainingStock_normalCase() {
        Integer itemId = 5;

        // top up 45, withdraw 10, order 5  -> 45 - 10 - 5 = 30
        when(inventoryRepository.sumQtyByItemIdAndType(itemId, "T")).thenReturn(45L);
        when(inventoryRepository.sumQtyByItemIdAndType(itemId, "W")).thenReturn(10L);
        when(orderRepository.sumOrderedQtyByItemId(itemId)).thenReturn(5L);

        long remaining = stockService.getRemainingStock(itemId);

        assertThat(remaining).isEqualTo(30L);

        verify(inventoryRepository).sumQtyByItemIdAndType(itemId, "T");
        verify(inventoryRepository).sumQtyByItemIdAndType(itemId, "W");
        verify(orderRepository).sumOrderedQtyByItemId(itemId);
    }

    @Test
    @DisplayName("getRemainingStock - tidak ada inventory movement dan order")
    void getRemainingStock_zeroCase() {
        Integer itemId = 1;

        when(inventoryRepository.sumQtyByItemIdAndType(itemId, "T")).thenReturn(0L);
        when(inventoryRepository.sumQtyByItemIdAndType(itemId, "W")).thenReturn(0L);
        when(orderRepository.sumOrderedQtyByItemId(itemId)).thenReturn(0L);

        long remaining = stockService.getRemainingStock(itemId);

        assertThat(remaining).isEqualTo(0L);

        verify(inventoryRepository).sumQtyByItemIdAndType(itemId, "T");
        verify(inventoryRepository).sumQtyByItemIdAndType(itemId, "W");
        verify(orderRepository).sumOrderedQtyByItemId(itemId);
    }
}