package com.obs.inventory.service.impl;

import com.obs.inventory.dto.ItemRequestDto;
import com.obs.inventory.dto.ItemResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.entity.ItemEntity;
import com.obs.inventory.exception.ErrorBusinessException;
import com.obs.inventory.repository.ItemRepository;
import com.obs.inventory.service.ItemService;
import com.obs.inventory.service.StockService;
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
public class ItemServiceImpl implements ItemService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private final ItemRepository itemRepository;

    private final StockService stockService;

    @Override
    public Page<ItemResponseDto> getItemsPage(Pageable pageable) {
        return itemRepository.findAll(pageable)
                .map(this::toDto);
    }

    @Override
    public ResponseMessage<ItemResponseDto> getItem(Integer id) {
        ItemEntity entity = itemRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ErrorBusinessException("Item not found", "ITEM-404"));

        ItemResponseDto dto = toDto(entity);
        return buildResponse(Collections.singletonList(dto), false,
                null, "Success get item");
    }

    @Override
    public ResponseMessage<ItemResponseDto> createItem(ItemRequestDto request) {
        validateItemRequest(request);

        if (itemRepository.existsById(String.valueOf(request.getId()))) {
            throw new ErrorBusinessException("Item ID already exists", "ITEM-001");
        }

        ItemEntity entity = ItemEntity.builder()
                .id(request.getId())
                .name(request.getName())
                .price(request.getPrice())
                .build();

        entity = itemRepository.save(entity);
        ItemResponseDto dto = toDto(entity);

        return buildResponse(Collections.singletonList(dto), false,
                null, "Item created successfully");
    }

    @Override
    public ResponseMessage<ItemResponseDto> updateItem(ItemRequestDto request) {
        validateItemRequest(request);

        ItemEntity entity = itemRepository.findById(String.valueOf(request.getId()))
                .orElseThrow(() -> new ErrorBusinessException("Item not found", "ITEM-404"));

        entity.setName(request.getName());
        entity.setPrice(request.getPrice());

        entity = itemRepository.save(entity);
        ItemResponseDto dto = toDto(entity);

        return buildResponse(Collections.singletonList(dto), false,
                null, "Item updated successfully");
    }

    @Override
    public ResponseMessage<Object> deleteItem(Integer id) {
        ItemEntity entity = itemRepository.findById(String.valueOf(id))
                .orElseThrow(() -> new ErrorBusinessException("Item not found", "ITEM-404"));

        itemRepository.delete(entity);

        return buildResponse(Collections.emptyList(), false,
                null, "Item deleted successfully");
    }

    // ---------------------HELPER---------------------

    private ItemResponseDto toDto(ItemEntity entity) {
        long remaining = stockService.getRemainingStock(entity.getId());
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPrice(entity.getPrice());
        dto.setRemainingStock(remaining);
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

    private void validateItemRequest(ItemRequestDto request) {
        if (request == null) {
            throw new ErrorBusinessException("Request body cannot be null", "ITEM-VAL-000");
        }
        if (request.getId() == null) {
            throw new ErrorBusinessException("Item ID is required", "ITEM-VAL-001");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new ErrorBusinessException("Item name is required", "ITEM-VAL-002");
        }
        if (request.getPrice() == null) {
            throw new ErrorBusinessException("Item price is required", "ITEM-VAL-003");
        }
        if (request.getPrice().signum() <= 0) {
            throw new ErrorBusinessException("Item price must be greater than 0", "ITEM-VAL-004");
        }
    }
}
