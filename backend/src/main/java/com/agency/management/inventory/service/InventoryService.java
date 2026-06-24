package com.agency.management.inventory.service;

import com.agency.management.common.FilterDto;
import com.agency.management.inventory.dto.request.InventoryExchangeDto;
import com.agency.management.inventory.dto.request.InventoryStockDto;
import com.agency.management.inventory.dto.request.NewConnectionDto;
import org.springframework.http.ResponseEntity;

public interface InventoryService {

    ResponseEntity<?> createNewConnection(NewConnectionDto newConnection);

    ResponseEntity<?> getNewConnectionLists(FilterDto filterDto);

    ResponseEntity<?> deleteNewConnection(Long connectionId);

    ResponseEntity<?> addUpdateInventory(InventoryStockDto inventoryStockDto);

    ResponseEntity<?> getAllLiveInventory();

    ResponseEntity<?> getLiveInventoryList(FilterDto filterDto);

    ResponseEntity<?> recordInventoryExchange(InventoryExchangeDto dto);

    ResponseEntity<?> getDetailsOfCustomer(Long customerId);

}
