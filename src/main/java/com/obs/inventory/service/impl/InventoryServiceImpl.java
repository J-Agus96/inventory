package com.obs.inventory.service.impl;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.entity.InventoryEntity;
import com.obs.inventory.entity.ItemEntity;
import com.obs.inventory.exception.ErrorBusinessException;
import com.obs.inventory.repository.InventoryRepository;
import com.obs.inventory.repository.ItemRepository;
import com.obs.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final InventoryRepository inventoryRepository;

    private final ItemRepository itemRepository;

    @Override
    public Page<InventoryResponseDto> getInventoriesPage(Pageable pageable) {
        return inventoryRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public ResponseMessage<InventoryResponseDto> getInventory(Integer id) {
        InventoryEntity entity = inventoryRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ErrorBusinessException("Inventory not found", "INV-404"));

        return buildResponse(Collections.singletonList(toDto(entity)), false, null,
                "Success get inventory");
    }

    @Override
    public ResponseMessage<InventoryResponseDto> createInventory(InventoryRequestDto request) {
        validateInventoryRequest(request);

        if (inventoryRepository.existsById(String.valueOf(request.getId()))) {
            throw new ErrorBusinessException("Inventory ID already exists", "INV-001");
        }

        ItemEntity item = itemRepository.findById(String.valueOf(request.getItemId()))
                .orElseThrow(() -> new ErrorBusinessException("Item not found", "INV-ITEM-404"));

        InventoryEntity entity = InventoryEntity.builder()
                .id(request.getId())
                .item(item)
                .qty(request.getQty())
                .type(request.getType().trim().toUpperCase())
                .build();

        entity = inventoryRepository.save(entity);

        return buildResponse(Collections.singletonList(toDto(entity)), false, null,
                "Inventory created successfully");
    }

    @Override
    public ResponseMessage<InventoryResponseDto> updateInventory(InventoryRequestDto request) {
        validateInventoryRequest(request);

        InventoryEntity entity = inventoryRepository.findById(String.valueOf(request.getId()))
                .orElseThrow(() -> new ErrorBusinessException("Inventory not found", "INV-404"));

        ItemEntity item = itemRepository.findById(String.valueOf(request.getItemId()))
                .orElseThrow(() -> new ErrorBusinessException("Item not found", "INV-ITEM-404"));

        entity.setItem(item);
        entity.setQty(request.getQty());
        entity.setType(request.getType().trim().toUpperCase());

        entity = inventoryRepository.save(entity);

        return buildResponse(Collections.singletonList(toDto(entity)), false, null,
                "Inventory updated successfully");
    }

    @Override
    public ResponseMessage<Object> deleteInventory(Integer id) {
        InventoryEntity entity = inventoryRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ErrorBusinessException("Inventory not found", "INV-404"));

        inventoryRepository.delete(entity);
        return buildResponse(Collections.emptyList(), false, null,
                "Inventory deleted successfully");
    }

    // =================HELPER=====================

    private InventoryResponseDto toDto(InventoryEntity entity) {
        InventoryResponseDto dto = new InventoryResponseDto();
        dto.setId(entity.getId());
        dto.setItemId(entity.getItem().getId());
        dto.setQty(entity.getQty());
        dto.setType(entity.getType());
        return dto;
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

    private void validateInventoryRequest(InventoryRequestDto request) {
        if (request == null) {
            throw new ErrorBusinessException("Request body cannot be null", "INV-VAL-000");
        }
        if (request.getId() == null) {
            throw new ErrorBusinessException("Inventory ID is required", "INV-VAL-001");
        }
        if (request.getItemId() == null) {
            throw new ErrorBusinessException("Item ID is required", "INV-VAL-002");
        }
        if (request.getQty() == null) {
            throw new ErrorBusinessException("Quantity is required", "INV-VAL-003");
        }
        if (request.getQty() <= 0) {
            throw new ErrorBusinessException("Quantity must be greater than 0", "INV-VAL-004");
        }
        if (request.getType() == null || request.getType().trim().isEmpty()) {
            throw new ErrorBusinessException("Inventory type is required", "INV-VAL-005");
        }
        String type = request.getType().trim().toUpperCase();
        if (!type.equals("T") && !type.equals("W")) {
            throw new ErrorBusinessException("Inventory type must be 'T' or 'W'", "INV-VAL-006");
        }
        request.setType(type);
    }
}
