package com.obs.inventory.service.impl;

import com.obs.inventory.repository.InventoryRepository;
import com.obs.inventory.repository.OrderRepository;
import com.obs.inventory.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final InventoryRepository inventoryRepository;

    private final OrderRepository orderRepository;

    @Override
    public long getRemainingStock(Integer itemId) {
        long topUp = inventoryRepository.sumQtyByItemIdAndType(itemId, "T");
        long withdraw = inventoryRepository.sumQtyByItemIdAndType(itemId, "W");
        long order = orderRepository.sumOrderedQtyByItemId(itemId);
        return topUp - withdraw - order;
    }
}