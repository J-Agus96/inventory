package com.obs.inventory.repository;

import com.obs.inventory.entity.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryEntity, String>, JpaSpecificationExecutor<InventoryEntity> {

    @Query("select coalesce(sum(i.qty), 0) " +
            "from InventoryEntity i " +
            "where i.item.id = :itemId and i.type = :type")
    long sumQtyByItemIdAndType(@Param("itemId") Integer itemId,
                               @Param("type") String type);
}
