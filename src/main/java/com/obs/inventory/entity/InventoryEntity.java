package com.obs.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id_fk", nullable = false)
    private ItemEntity item;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "qty", nullable = false)
    private Long qty;

    @Column(name = "type", nullable = false, length = 1)
    private String type;

}
