package com.obs.inventory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private String id;

    @Column(name = "order_no", nullable = false)
    private String orderNo;               // O1..O10

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id_fk", nullable = false)
    private ItemEntity item;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "qty", nullable = false)
    private Long qty;

    @Column(name = "price", nullable = false)
    private BigDecimal price;
}
