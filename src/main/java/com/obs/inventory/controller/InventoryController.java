package com.obs.inventory.controller;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventories")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("")
    public Page<InventoryResponseDto> getInventoriesPage(Pageable pageable) {
        return inventoryService.getInventoriesPage(pageable);
    }

    @GetMapping("/{id}")
    public ResponseMessage<InventoryResponseDto> getInventory(@PathVariable Integer id) {
        return inventoryService.getInventory(id);
    }

    @PostMapping("/create")
    public ResponseMessage<InventoryResponseDto> createInventory(@Valid @RequestBody InventoryRequestDto inventoryRequestDto) {
        return inventoryService.createInventory(inventoryRequestDto);
    }

    @PutMapping("/update")
    public ResponseMessage<InventoryResponseDto> updateInventory(@Valid @RequestBody InventoryRequestDto inventoryRequestDto) {
        return inventoryService.updateInventory(inventoryRequestDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage<Object> deleteInventory(@PathVariable Integer id) {
        return inventoryService.deleteInventory(id);
    }

}
