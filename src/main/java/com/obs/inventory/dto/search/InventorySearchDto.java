package com.obs.inventory.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventorySearchDto {

    private Integer id;

    private Integer itemId;

    private String type;
}
