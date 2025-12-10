package com.obs.inventory.service;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.InventorySearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryService {

    Page<InventoryResponseDto> getInventoriesPage(InventorySearchDto inventorySearchDto, Pageable pageable);

    ResponseMessage<InventoryResponseDto> getInventory(Integer id);

    ResponseMessage<InventoryResponseDto> createInventory(InventoryRequestDto request);

    ResponseMessage<InventoryResponseDto> updateInventory(InventoryRequestDto request);

    ResponseMessage<Object> deleteInventory(Integer id);

}
