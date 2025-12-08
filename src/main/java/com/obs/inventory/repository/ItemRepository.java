package com.obs.inventory.repository;

import com.obs.inventory.entity.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, String>, JpaSpecificationExecutor<ItemEntity> {

    Optional<ItemEntity> findByItemId(Integer itemId);
}
