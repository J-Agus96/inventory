package com.obs.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    @NotNull
    private Integer itemId;

    @NotBlank
    private String name;

    @NotNull
    @Positive
    private BigDecimal price;

}
