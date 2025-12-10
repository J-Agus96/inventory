package com.obs.inventory.controller;

import com.obs.inventory.dto.ItemRequestDto;
import com.obs.inventory.dto.ItemResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.ItemSearchDto;
import com.obs.inventory.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping("")
    public Page<ItemResponseDto> getItemsPage (ItemSearchDto itemSearchDto, Pageable pageable){
        return itemService.getItemsPage(itemSearchDto, pageable);
    }

    @GetMapping("/{id}")
    public ResponseMessage<ItemResponseDto> getItem (@PathVariable Integer id){
        return itemService.getItem(id);
    }

    @PostMapping("/create")
    public ResponseMessage<ItemResponseDto> createItem (@Valid @RequestBody ItemRequestDto itemRequestDto){
        return itemService.createItem(itemRequestDto);
    }

    @PutMapping("/update")
    public ResponseMessage<ItemResponseDto> updateItem (@Valid @RequestBody ItemRequestDto itemRequestDto){
        return itemService.updateItem(itemRequestDto);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseMessage<Object> deleteItem (@PathVariable Integer id){
        return itemService.deleteItem(id);
    }

}
