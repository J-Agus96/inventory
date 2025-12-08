package com.obs.inventory.service.impl;

import com.obs.inventory.dto.ItemRequestDto;
import com.obs.inventory.dto.ItemResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.repository.ItemRepository;
import com.obs.inventory.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    @Override
    public Page<ItemResponseDto> getItems(Pageable pageable) {
        return null;
    }

    @Override
    public ResponseMessage<ItemResponseDto> getItem(String id) {
        return null;
    }

    @Override
    public ResponseMessage<ItemResponseDto> createItem(ItemRequestDto request) {
        return null;
    }

    @Override
    public ResponseMessage<ItemResponseDto> updateItem(String id, ItemRequestDto request) {
        return null;
    }

    @Override
    public ResponseMessage<Object> deleteItem(String id) {
        return null;
    }
}
