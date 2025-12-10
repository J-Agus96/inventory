package com.obs.inventory.controller;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.InventorySearchDto;
import com.obs.inventory.service.InventoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    // ===== helper =====
    private InventoryResponseDto buildResponseDto(Integer id, Integer itemId, Long qty, String type) {
        InventoryResponseDto dto = new InventoryResponseDto();
        dto.setId(id);
        dto.setItemId(itemId);
        dto.setQty(qty);
        dto.setType(type);
        return dto;
    }

    private InventoryRequestDto buildRequest(Integer id, Integer itemId, Long qty, String type) {
        InventoryRequestDto dto = new InventoryRequestDto();
        dto.setId(id);
        dto.setItemId(itemId);
        dto.setQty(qty);
        dto.setType(type);
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

    // ===== getInventoriesPage =====

    @Test
    @DisplayName("getInventoriesPage - delegasi ke service dan mengembalikan page yang sama")
    void getInventoriesPage_shouldDelegateToService() {
        InventorySearchDto search = new InventorySearchDto();
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        InventoryResponseDto dto = buildResponseDto(1, 5, 10L, "T");
        Page<InventoryResponseDto> page = new PageImpl<>(List.of(dto), pageable, 1);

        when(inventoryService.getInventoriesPage(any(InventorySearchDto.class), any(Pageable.class)))
                .thenReturn(page);

        Page<InventoryResponseDto> result =
                inventoryController.getInventoriesPage(search, pageable);

        assertThat(result).isSameAs(page);
        verify(inventoryService).getInventoriesPage(search, pageable);
    }

    // ===== getInventory =====

    @Test
    @DisplayName("getInventory - delegasi ke service dan kembalikan response yang sama")
    void getInventory_shouldDelegateToService() {
        InventoryResponseDto dto = buildResponseDto(1, 5, 10L, "T");
        ResponseMessage<InventoryResponseDto> resp =
                wrap(dto, "Success get inventory");

        when(inventoryService.getInventory(1)).thenReturn(resp);

        ResponseMessage<InventoryResponseDto> result =
                inventoryController.getInventory(1);

        assertThat(result).isSameAs(resp);
        verify(inventoryService).getInventory(1);
    }

    // ===== createInventory =====

    @Test
    @DisplayName("createInventory - delegasi ke service")
    void createInventory_shouldDelegateToService() {
        InventoryRequestDto req = buildRequest(1, 5, 10L, "T");
        InventoryResponseDto dto = buildResponseDto(1, 5, 10L, "T");
        ResponseMessage<InventoryResponseDto> resp =
                wrap(dto, "Inventory created successfully");

        when(inventoryService.createInventory(req)).thenReturn(resp);

        ResponseMessage<InventoryResponseDto> result =
                inventoryController.createInventory(req);

        assertThat(result).isSameAs(resp);
        verify(inventoryService).createInventory(req);
    }

    // ===== updateInventory =====

    @Test
    @DisplayName("updateInventory - delegasi ke service")
    void updateInventory_shouldDelegateToService() {
        InventoryRequestDto req = buildRequest(1, 5, 20L, "W");
        InventoryResponseDto dto = buildResponseDto(1, 5, 20L, "W");
        ResponseMessage<InventoryResponseDto> resp =
                wrap(dto, "Inventory updated successfully");

        when(inventoryService.updateInventory(req)).thenReturn(resp);

        ResponseMessage<InventoryResponseDto> result =
                inventoryController.updateInventory(req);

        assertThat(result).isSameAs(resp);
        verify(inventoryService).updateInventory(req);
    }

    // ===== deleteInventory =====

    @Test
    @DisplayName("deleteInventory - delegasi ke service")
    void deleteInventory_shouldDelegateToService() {
        ResponseMessage<Object> resp = new ResponseMessage<>();
        resp.setIsError(false);
        resp.setMessage("Inventory deleted successfully");

        when(inventoryService.deleteInventory(1)).thenReturn(resp);

        ResponseMessage<Object> result =
                inventoryController.deleteInventory(1);

        assertThat(result).isSameAs(resp);
        verify(inventoryService).deleteInventory(1);
    }
}