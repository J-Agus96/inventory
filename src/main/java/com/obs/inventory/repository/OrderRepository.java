package com.obs.inventory.repository;

import com.obs.inventory.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, String>, JpaSpecificationExecutor<OrderEntity> {

    @Query("select coalesce(sum(o.qty), 0) " +
            "from OrderEntity o " +
            "where o.item.id = :itemId")
    long sumOrderedQtyByItemId(@Param("itemId") Integer itemId);
}
