package com.obs.inventory.service.impl;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.repository.InventoryRepository;
import com.obs.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;

    @Override
    public Page<InventoryResponseDto> getInventories(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseMessage<InventoryResponseDto> getInventory(String id) {
        return null;
    }

    @Override
    public ResponseMessage<InventoryResponseDto> createInventory(InventoryRequestDto request) {
        return null;
    }

    @Override
    public ResponseMessage<InventoryResponseDto> updateInventory(String id, InventoryRequestDto request) {
        return null;
    }

    @Override
    public ResponseMessage<Object> deleteInventory(String id) {
        return null;
    }
}
