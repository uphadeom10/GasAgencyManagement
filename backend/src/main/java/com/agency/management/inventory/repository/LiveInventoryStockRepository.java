package com.agency.management.inventory.repository;

import com.agency.management.inventory.dto.response.LiveInventoryResponseList;
import com.agency.management.inventory.entity.LiveInventoryStocks;
import com.agency.management.masters.entity.ProductCategory;
import com.agency.management.masters.entity.Products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LiveInventoryStockRepository extends JpaRepository<LiveInventoryStocks, Long> {

    Optional<LiveInventoryStocks> findByProductIdAndProductCategoryId(Products products, ProductCategory productCategory);

    @Query(value = "select * from fn_get_live_Inventory(?1,?2,?3,?4)",nativeQuery = true)
    List<LiveInventoryResponseList> getLiveInventory(Long id, String searchString, Integer page, Integer size);

    @Query(value = "select * from fn_get_live_inventory_count(?1,?2)",nativeQuery = true)
    Long getLiveInventoryCount(Long id, String searchString);

    Optional<LiveInventoryStocks> findByProductId_Id(Products products);

    Optional<LiveInventoryStocks> findByProductId(Products productsId);
}