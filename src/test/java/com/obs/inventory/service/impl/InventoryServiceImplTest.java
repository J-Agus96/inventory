package com.obs.inventory.service.impl;

import com.obs.inventory.dto.InventoryRequestDto;
import com.obs.inventory.dto.InventoryResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.InventorySearchDto;
import com.obs.inventory.entity.InventoryEntity;
import com.obs.inventory.entity.ItemEntity;
import com.obs.inventory.exception.ErrorBusinessException;
import com.obs.inventory.repository.InventoryRepository;
import com.obs.inventory.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    // ========= helper =========

    private ItemEntity buildItem(Integer id, String name, BigDecimal price) {
        return ItemEntity.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
    }

    private InventoryEntity buildInventory(Integer id, ItemEntity item, Long qty, String type) {
        return InventoryEntity.builder()
                .id(id)
                .item(item)
                .qty(qty)
                .type(type)
                .build();
    }

    private InventoryRequestDto buildRequest(Integer id, Integer itemId, Long qty, String type) {
        InventoryRequestDto dto = new InventoryRequestDto();
        dto.setId(id);
        dto.setItemId(itemId);
        dto.setQty(qty);
        dto.setType(type);
        return dto;
    }

    // ========= getInventoriesPage =========

    @Test
    @DisplayName("getInventoriesPage - tanpa filter (hanya pageable)")
    void getInventoriesPage_noFilter() {
        InventorySearchDto search = new InventorySearchDto(); // id, itemId, type null
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        ItemEntity item = buildItem(5, "Shoe", BigDecimal.valueOf(45));
        InventoryEntity inv = buildInventory(1, item, 5L, "T");

        when(inventoryRepository.findAll(
                org.mockito.Mockito.<Specification<InventoryEntity>>any(),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(inv), pageable, 1));

        Page<InventoryResponseDto> page = inventoryService.getInventoriesPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        InventoryResponseDto dto = page.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getItemId()).isEqualTo(5);
        assertThat(dto.getQty()).isEqualTo(5);
        assertThat(dto.getType()).isEqualTo("T");

        verify(inventoryRepository).findAll(
                org.mockito.Mockito.<Specification<InventoryEntity>>any(),
                eq(pageable)
        );
    }

    @Test
    @DisplayName("getInventoriesPage - filter hanya ID")
    void getInventoriesPage_filterById() {
        InventorySearchDto search = new InventorySearchDto();
        search.setId(2);
        Pageable pageable = PageRequest.of(0, 10);

        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        InventoryEntity inv = buildInventory(2, item, 10L, "T");

        when(inventoryRepository.findAll(
                org.mockito.Mockito.<Specification<InventoryEntity>>any(),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(inv), pageable, 1));

        Page<InventoryResponseDto> page = inventoryService.getInventoriesPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("getInventoriesPage - filter hanya itemId")
    void getInventoriesPage_filterByItemId() {
        InventorySearchDto search = new InventorySearchDto();
        search.setItemId(5);
        Pageable pageable = PageRequest.of(0, 10);

        ItemEntity item = buildItem(5, "Shoe", BigDecimal.valueOf(45));
        InventoryEntity inv = buildInventory(3, item, 45L, "T");

        when(inventoryRepository.findAll(
                org.mockito.Mockito.<Specification<InventoryEntity>>any(),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(inv), pageable, 1));

        Page<InventoryResponseDto> page = inventoryService.getInventoriesPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getItemId()).isEqualTo(5);
    }

    @Test
    @DisplayName("getInventoriesPage - filter hanya type (case-insensitive)")
    void getInventoriesPage_filterByType() {
        InventorySearchDto search = new InventorySearchDto();
        search.setType("t"); // harus diperlakukan sama dengan "T"
        Pageable pageable = PageRequest.of(0, 10);

        ItemEntity item = buildItem(1, "Pen", BigDecimal.valueOf(5));
        InventoryEntity inv = buildInventory(4, item, 5L, "T");

        when(inventoryRepository.findAll(
                org.mockito.Mockito.<Specification<InventoryEntity>>any(),
                eq(pageable)
        )).thenReturn(new PageImpl<>(List.of(inv), pageable, 1));

        Page<InventoryResponseDto> page = inventoryService.getInventoriesPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getType()).isEqualTo("T");
    }

    // ========= getInventory =========

    @Test
    @DisplayName("getInventory - sukses")
    void getInventory_success() {
        ItemEntity item = buildItem(5, "Shoe", BigDecimal.valueOf(45));
        InventoryEntity inv = buildInventory(1, item, 45L, "T");

        when(inventoryRepository.findById("1")).thenReturn(Optional.of(inv));

        ResponseMessage<InventoryResponseDto> resp = inventoryService.getInventory(1);

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getErrorNumber()).isNull();
        assertThat(resp.getMessage()).isEqualTo("Success get inventory");
        assertThat(resp.getData()).hasSize(1);

        InventoryResponseDto dto = resp.getData().get(0);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getItemId()).isEqualTo(5);
        assertThat(dto.getQty()).isEqualTo(45);
        assertThat(dto.getType()).isEqualTo("T");

        verify(inventoryRepository).findById("1");
    }

    @Test
    @DisplayName("getInventory - tidak ditemukan -> INV-404")
    void getInventory_notFound() {
        when(inventoryRepository.findById("99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.getInventory(99))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Inventory not found")
                .extracting("errorNumber")
                .isEqualTo("INV-404");
    }

    // ========= createInventory =========

    @Test
    @DisplayName("createInventory - sukses")
    void createInventory_success() {
        InventoryRequestDto req = buildRequest(1, 5, 10L, "t");

        ItemEntity item = buildItem(5, "Shoe", BigDecimal.valueOf(45));

        when(inventoryRepository.existsById("1")).thenReturn(false);
        when(itemRepository.findById("5")).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(InventoryEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ResponseMessage<InventoryResponseDto> resp = inventoryService.createInventory(req);

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Inventory created successfully");
        assertThat(resp.getData()).hasSize(1);

        InventoryResponseDto dto = resp.getData().get(0);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getItemId()).isEqualTo(5);
        assertThat(dto.getQty()).isEqualTo(10);
        assertThat(dto.getType()).isEqualTo("T"); // harus sudah uppercase

        ArgumentCaptor<InventoryEntity> captor = ArgumentCaptor.forClass(InventoryEntity.class);
        verify(inventoryRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo("T");
    }

    @Test
    @DisplayName("createInventory - ID sudah ada -> INV-001")
    void createInventory_idAlreadyExists() {
        InventoryRequestDto req = buildRequest(1, 5, 10L, "T");

        when(inventoryRepository.existsById("1")).thenReturn(true);

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Inventory ID already exists")
                .extracting("errorNumber")
                .isEqualTo("INV-001");
    }

    @Test
    @DisplayName("createInventory - item tidak ditemukan -> INV-ITEM-404")
    void createInventory_itemNotFound() {
        InventoryRequestDto req = buildRequest(1, 5, 10L, "T");

        when(inventoryRepository.existsById("1")).thenReturn(false);
        when(itemRepository.findById("5")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item not found")
                .extracting("errorNumber")
                .isEqualTo("INV-ITEM-404");
    }

    // ========= deleteInventory =========

    @Test
    @DisplayName("deleteInventory - sukses")
    void deleteInventory_success() {
        ItemEntity item = buildItem(5, "Shoe", BigDecimal.valueOf(45));
        InventoryEntity inv = buildInventory(1, item, 45L, "T");

        when(inventoryRepository.findById("1")).thenReturn(Optional.of(inv));

        ResponseMessage<Object> resp = inventoryService.deleteInventory(1);

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Inventory deleted successfully");
        verify(inventoryRepository).delete(inv);
    }

    // ========= validateInventoryRequest (via createInventory) =========

    @Test
    @DisplayName("validateInventoryRequest - request null -> INV-VAL-000")
    void createInventory_requestNull() {
        assertThatThrownBy(() -> inventoryService.createInventory(null))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Request body cannot be null")
                .extracting("errorNumber")
                .isEqualTo("INV-VAL-000");
    }

    @Test
    @DisplayName("validateInventoryRequest - id null -> INV-VAL-001")
    void createInventory_idNull() {
        InventoryRequestDto req = buildRequest(null, 5, 10L, "T");

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Inventory ID is required")
                .extracting("errorNumber")
                .isEqualTo("INV-VAL-001");
    }

    @Test
    @DisplayName("validateInventoryRequest - itemId null -> INV-VAL-002")
    void createInventory_itemIdNull() {
        InventoryRequestDto req = buildRequest(1, null, 10L, "T");

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item ID is required")
                .extracting("errorNumber")
                .isEqualTo("INV-VAL-002");
    }

    @Test
    @DisplayName("validateInventoryRequest - qty null -> INV-VAL-003")
    void createInventory_qtyNull() {
        InventoryRequestDto req = buildRequest(1, 5, null, "T");

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Quantity is required")
                .extracting("errorNumber")
                .isEqualTo("INV-VAL-003");
    }

    @Test
    @DisplayName("validateInventoryRequest - qty <= 0 -> INV-VAL-004")
    void createInventory_qtyLessOrEqualZero() {
        InventoryRequestDto req = buildRequest(1, 5, 0L, "T");

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Quantity must be greater than 0")
                .extracting("errorNumber")
                .isEqualTo("INV-VAL-004");
    }

    @Test
    @DisplayName("validateInventoryRequest - type kosong -> INV-VAL-005")
    void createInventory_typeBlank() {
        InventoryRequestDto req = buildRequest(1, 5, 10L, "   ");

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Inventory type is required")
                .extracting("errorNumber")
                .isEqualTo("INV-VAL-005");
    }

    @Test
    @DisplayName("validateInventoryRequest - type selain T/W -> INV-VAL-006")
    void createInventory_typeInvalid() {
        InventoryRequestDto req = buildRequest(1, 5, 10L, "X");

        assertThatThrownBy(() -> inventoryService.createInventory(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Inventory type must be 'T' or 'W'")
                .extracting("errorNumber")
                .isEqualTo("INV-VAL-006");
    }
}
