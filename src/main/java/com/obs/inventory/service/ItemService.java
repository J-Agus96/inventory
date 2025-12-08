package com.obs.inventory.service;

import com.obs.inventory.dto.ItemRequestDto;
import com.obs.inventory.dto.ItemResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    Page<ItemResponseDto> getItems(Pageable pageable);         // listing + remaining stock

    ResponseMessage<ItemResponseDto> getItem(String id);       // get detail + remaining stock

    ResponseMessage<ItemResponseDto> createItem(ItemRequestDto request);

    ResponseMessage<ItemResponseDto> updateItem(String id, ItemRequestDto request);

    ResponseMessage<Object> deleteItem(String id);

}
