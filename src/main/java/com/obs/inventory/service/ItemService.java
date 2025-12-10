package com.obs.inventory.service;

import com.obs.inventory.dto.ItemRequestDto;
import com.obs.inventory.dto.ItemResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.ItemSearchDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    Page<ItemResponseDto> getItemsPage(ItemSearchDto itemSearchDto, Pageable pageable);         // listing + remaining stock

    ResponseMessage<ItemResponseDto> getItem(Integer id);       // get detail + remaining stock

    ResponseMessage<ItemResponseDto> createItem(ItemRequestDto request);

    ResponseMessage<ItemResponseDto> updateItem(ItemRequestDto request);

    ResponseMessage<Object> deleteItem(Integer id);

}
