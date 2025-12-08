package com.obs.inventory.service;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    Page<InventoryResponseDto> getInventories(Pageable pageable);

    ResponseMessage<InventoryResponseDto> getInventory(String id);

    ResponseMessage<InventoryResponseDto> createInventory(InventoryRequestDto request);

    ResponseMessage<InventoryResponseDto> updateInventory(String id, InventoryRequestDto request);

    ResponseMessage<Object> deleteInventory(String id);

}
