package com.obs.inventory.service.impl;

import com.obs.inventory.dto.ItemRequestDto;
import com.obs.inventory.dto.ItemResponseDto;
import com.obs.inventory.dto.response.ResponseMessage;
import com.obs.inventory.dto.search.ItemSearchDto;
import com.obs.inventory.entity.ItemEntity;
import com.obs.inventory.exception.ErrorBusinessException;
import com.obs.inventory.repository.ItemRepository;
import com.obs.inventory.service.StockService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockService stockService;

    @InjectMocks
    private ItemServiceImpl itemService;

    // ---------- helper ----------

    private ItemEntity buildItem(Integer id, String name, BigDecimal price) {
        return ItemEntity.builder()
                .id(id)
                .name(name)
                .price(price)
                .build();
    }

    private ItemRequestDto buildRequest(Integer id, String name, BigDecimal price) {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(id);
        dto.setName(name);
        dto.setPrice(price);
        return dto;
    }

    // ---------- getItemsPage ----------

    @Test
    @DisplayName("getItemsPage - tanpa filter (hanya pageable)")
    void getItemsPage_noFilter() {
        ItemSearchDto search = new ItemSearchDto(); // id & name null
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        ItemEntity entity = buildItem(1, "Pen", BigDecimal.valueOf(5));

        when(itemRepository.findAll(Mockito.<Specification<ItemEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        when(stockService.getRemainingStock(1)).thenReturn(10L);

        Page<ItemResponseDto> page = itemService.getItemsPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        ItemResponseDto dto = page.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Pen");
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(5));
        assertThat(dto.getRemainingStock()).isEqualTo(10L);

        verify(itemRepository).findAll(Mockito.<Specification<ItemEntity>>any(), eq(pageable));
    }

    @Test
    @DisplayName("getItemsPage - result kosong (tidak ada data)")
    void getItemsPage_emptyResult() {
        ItemSearchDto search = new ItemSearchDto();
        search.setName("unknown");
        Pageable pageable = PageRequest.of(0, 10);

        when(itemRepository.findAll(Mockito.<Specification<ItemEntity>>any(), eq(pageable)))
                .thenReturn(Page.empty(pageable));

        Page<ItemResponseDto> page = itemService.getItemsPage(search, pageable);

        assertThat(page.getTotalElements()).isZero();
        assertThat(page.getContent()).isEmpty();

        verify(stockService, never()).getRemainingStock(anyInt());
    }

    @Test
    @DisplayName("getItemsPage - filter hanya ID")
    void getItemsPage_filterById() {
        ItemSearchDto search = new ItemSearchDto();
        search.setId(2);
        Pageable pageable = PageRequest.of(0, 10);

        ItemEntity entity = buildItem(2, "Book", BigDecimal.TEN);
        when(itemRepository.findAll(Mockito.<Specification<ItemEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        when(stockService.getRemainingStock(2)).thenReturn(20L);

        Page<ItemResponseDto> page = itemService.getItemsPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("getItemsPage - filter hanya name (like, ignore case)")
    void getItemsPage_filterByName() {
        ItemSearchDto search = new ItemSearchDto();
        search.setName("pen"); // akan jadi %pen% lower-case
        Pageable pageable = PageRequest.of(0, 10);

        ItemEntity entity = buildItem(1, "Pen", BigDecimal.valueOf(5));
        when(itemRepository.findAll(Mockito.<Specification<ItemEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        when(stockService.getRemainingStock(1)).thenReturn(5L);

        Page<ItemResponseDto> page = itemService.getItemsPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).getName().toLowerCase(Locale.ROOT))
                .contains("pen");
    }

    @Test
    @DisplayName("getItemsPage - filter ID dan name sekaligus")
    void getItemsPage_filterIdAndName() {
        ItemSearchDto search = new ItemSearchDto();
        search.setId(3);
        search.setName("bag");

        Pageable pageable = PageRequest.of(0, 10);
        ItemEntity entity = buildItem(3, "Bag", BigDecimal.valueOf(30));
        when(itemRepository.findAll(Mockito.<Specification<ItemEntity>>any(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(entity), pageable, 1));
        when(stockService.getRemainingStock(3)).thenReturn(30L);

        Page<ItemResponseDto> page = itemService.getItemsPage(search, pageable);

        assertThat(page.getTotalElements()).isEqualTo(1);
        ItemResponseDto dto = page.getContent().get(0);
        assertThat(dto.getId()).isEqualTo(3);
        assertThat(dto.getName()).isEqualTo("Bag");
    }

    @Test
    @DisplayName("getItem - item tidak ditemukan -> ITEM-404")
    void getItem_notFound() {
        when(itemRepository.findById("99")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemService.getItem(99))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item not found")
                .extracting("errorNumber")
                .isEqualTo("ITEM-404");
    }

    // ---------- create / update / delete / get ----------

    @Test
    @DisplayName("createItem - sukses")
    void createItem_success() {
        ItemRequestDto req = buildRequest(1, "Pen", BigDecimal.valueOf(5));

        when(itemRepository.existsById("1")).thenReturn(false);
        when(itemRepository.save(any(ItemEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockService.getRemainingStock(1)).thenReturn(5L);

        ResponseMessage<ItemResponseDto> resp = itemService.createItem(req);

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Item created successfully");
        assertThat(resp.getData()).hasSize(1);
        ItemResponseDto dto = resp.getData().get(0);

        assertThat(dto.getId()).isEqualTo(1);
        assertThat(dto.getName()).isEqualTo("Pen");
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(5));

        ArgumentCaptor<ItemEntity> entityCaptor = ArgumentCaptor.forClass(ItemEntity.class);
        verify(itemRepository).save(entityCaptor.capture());
        assertThat(entityCaptor.getValue().getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("createItem - ID sudah ada -> ErrorBusinessException ITEM-001")
    void createItem_idAlreadyExists() {
        ItemRequestDto req = buildRequest(1, "Pen", BigDecimal.valueOf(5));
        when(itemRepository.existsById("1")).thenReturn(true);

        assertThatThrownBy(() -> itemService.createItem(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item ID already exists")
                .extracting("errorNumber")
                .isEqualTo("ITEM-001");
    }

    @Test
    @DisplayName("updateItem - sukses")
    void updateItem_success() {
        ItemRequestDto req = buildRequest(1, "Pen Baru", BigDecimal.valueOf(7));
        ItemEntity existing = buildItem(1, "Pen", BigDecimal.valueOf(5));

        when(itemRepository.findById("1")).thenReturn(Optional.of(existing));
        when(itemRepository.save(any(ItemEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(stockService.getRemainingStock(1)).thenReturn(10L);

        ResponseMessage<ItemResponseDto> resp = itemService.updateItem(req);

        assertThat(resp.getIsError()).isFalse();
        ItemResponseDto dto = resp.getData().get(0);
        assertThat(dto.getName()).isEqualTo("Pen Baru");
        assertThat(dto.getPrice()).isEqualTo(BigDecimal.valueOf(7));
    }

    @Test
    @DisplayName("deleteItem - sukses")
    void deleteItem_success() {
        ItemEntity existing = buildItem(1, "Pen", BigDecimal.valueOf(5));
        when(itemRepository.findById("1")).thenReturn(Optional.of(existing));

        ResponseMessage<Object> resp = itemService.deleteItem(1);

        assertThat(resp.getIsError()).isFalse();
        assertThat(resp.getMessage()).isEqualTo("Item deleted successfully");
        verify(itemRepository).delete(existing);
    }

    // ---------- validateItemRequest branches (via createItem) ----------

    @Test
    @DisplayName("validateItemRequest - request null -> ITEM-VAL-000")
    void createItem_requestNull() {
        assertThatThrownBy(() -> itemService.createItem(null))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Request body cannot be null")
                .extracting("errorNumber")
                .isEqualTo("ITEM-VAL-000");
    }

    @Test
    @DisplayName("validateItemRequest - id null -> ITEM-VAL-001")
    void createItem_idNull() {
        ItemRequestDto req = buildRequest(null, "Pen", BigDecimal.valueOf(5));

        assertThatThrownBy(() -> itemService.createItem(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item ID is required")
                .extracting("errorNumber")
                .isEqualTo("ITEM-VAL-001");
    }

    @Test
    @DisplayName("validateItemRequest - name kosong -> ITEM-VAL-002")
    void createItem_nameBlank() {
        ItemRequestDto req = buildRequest(1, "   ", BigDecimal.valueOf(5));

        assertThatThrownBy(() -> itemService.createItem(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item name is required")
                .extracting("errorNumber")
                .isEqualTo("ITEM-VAL-002");
    }

    @Test
    @DisplayName("validateItemRequest - price null -> ITEM-VAL-003")
    void createItem_priceNull() {
        ItemRequestDto req = buildRequest(1, "Pen", null);

        assertThatThrownBy(() -> itemService.createItem(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item price is required")
                .extracting("errorNumber")
                .isEqualTo("ITEM-VAL-003");
    }

    @Test
    @DisplayName("validateItemRequest - price <= 0 -> ITEM-VAL-004")
    void createItem_priceLessOrEqualZero() {
        ItemRequestDto req = buildRequest(1, "Pen", BigDecimal.ZERO);

        assertThatThrownBy(() -> itemService.createItem(req))
                .isInstanceOf(ErrorBusinessException.class)
                .hasMessageContaining("Item price must be greater than 0")
                .extracting("errorNumber")
                .isEqualTo("ITEM-VAL-004");
    }
}
