package com.agency.management.inventory.repository;

import com.agency.management.inventory.entity.InventoryStocks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryStocksRepository extends JpaRepository<InventoryStocks, Long> {

}
