package com.obs.inventory.controller;

import com.obs.inventory.dto.ItemRequestDto;
import com.obs.inventory.dto.ItemResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.ItemSearchDto;
import com.obs.inventory.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @InjectMocks
    private ItemController itemController;

    // ===== helper =====
    private ItemResponseDto buildResponseDto(Integer id, String name, BigDecimal price, long remaining) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(id);
        dto.setName(name);
        dto.setPrice(price);
        dto.setRemainingStock(remaining);
        return dto;
    }

    private ItemRequestDto buildRequest(Integer id, String name, BigDecimal price) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setName(name);
        dto.setPrice(price);
        return dto;
    }

    private <T> ResponseMessage<T> wrap(T data, String message) {
        ResponseMessage<T> resp = new ResponseMessage<>();
        resp.setIsError(false);
        resp.setErrorNumber(null);
        resp.setMessage(message);
        resp.setData(List.of(data));
        resp.setTrxDateResponse("dummy-date");
        return resp;
    }

    // ===== getItemsPage =====

    @Test
    @DisplayName("getItemsPage - delegasi ke service dan mengembalikan page yang sama")
    void getItemsPage_shouldDelegateToService() {
        ItemSearchDto search = new ItemSearchDto();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        ItemResponseDto dto = buildResponseDto(1, "Pen", BigDecimal.valueOf(5), 10L);
        Page<ItemResponseDto> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(itemService.getItemsPage(any(ItemSearchDto.class), any(Pageable.class)))
                .thenReturn(page);

        Page<ItemResponseDto> result = itemController.getItemsPage(search, pageable);

        assertThat(result).isSameAs(page);
        verify(itemService).getItemsPage(search, pageable);
    }

    // ===== getItem =====

    @Test
    @DisplayName("getItem - delegasi ke service dan kembalikan response yang sama")
    void getItem_shouldDelegateToService() {
        ItemResponseDto dto = buildResponseDto(1, "Pen", BigDecimal.valueOf(5), 10L);
        ResponseMessage<ItemResponseDto> resp = wrap(dto, "Success get item");

        when(itemService.getItem(1)).thenReturn(resp);

        ResponseMessage<ItemResponseDto> result = itemController.getItem(1);

        assertThat(result).isSameAs(resp);
        verify(itemService).getItem(1);
    }

    // ===== createItem =====

    @Test
    @DisplayName("createItem - delegasi ke service")
    void createItem_shouldDelegateToService() {
        ItemRequestDto req = buildRequest(1, "Pen", BigDecimal.valueOf(5));
        ItemResponseDto dto = buildResponseDto(1, "Pen", BigDecimal.valueOf(5), 10L);
        ResponseMessage<ItemResponseDto> resp = wrap(dto, "Item created successfully");

        when(itemService.createItem(req)).thenReturn(resp);

        ResponseMessage<ItemResponseDto> result = itemController.createItem(req);

        assertThat(result).isSameAs(resp);
        verify(itemService).createItem(req);
    }

    // ===== updateItem =====

    @Test
    @DisplayName("updateItem - delegasi ke service")
    void updateItem_shouldDelegateToService() {
        ItemRequestDto req = buildRequest(1, "Pen Baru", BigDecimal.valueOf(7));
        ItemResponseDto dto = buildResponseDto(1, "Pen Baru", BigDecimal.valueOf(7), 10L);
        ResponseMessage<ItemResponseDto> resp = wrap(dto, "Item updated successfully");

        when(itemService.updateItem(req)).thenReturn(resp);

        ResponseMessage<ItemResponseDto> result = itemController.updateItem(req);

        assertThat(result).isSameAs(resp);
        verify(itemService).updateItem(req);
    }

    // ===== deleteItem =====

    @Test
    @DisplayName("deleteItem - delegasi ke service")
    void deleteItem_shouldDelegateToService() {
        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(false);
        resp.setMessage("Item deleted successfully");

        when(itemService.deleteItem(1)).thenReturn(resp);

        ResponseMessage<Object> result = itemController.deleteItem(1);

        assertThat(result).isSameAs(resp);
        verify(itemService).deleteItem(1);
    }
}