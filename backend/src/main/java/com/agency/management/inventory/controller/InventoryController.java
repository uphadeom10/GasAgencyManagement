package com.agency.management.inventory.controller;

import com.agency.management.common.FilterDto;
import com.agency.management.inventory.dto.request.InventoryExchangeDto;
import com.agency.management.inventory.dto.request.InventoryStockDto;
import com.agency.management.inventory.dto.request.NewConnectionDto;
import com.agency.management.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/newConnection")
    public ResponseEntity<?> createNewConnection(@RequestBody NewConnectionDto newConnectionDto){
        return inventoryService.createNewConnection(newConnectionDto);
    }

    @PostMapping("/connectionList")
    public ResponseEntity<?> getConnectionList(@RequestBody FilterDto filterDto){
        return inventoryService.getNewConnectionLists(filterDto);
    }

    @DeleteMapping("/deleteConnection/{newConnectionId}")
    public ResponseEntity<?> deleteNewConnection(@PathVariable Long newConnectionId){
        return inventoryService.deleteNewConnection(newConnectionId);
    }

    @PostMapping("/addOrUpdate")
    public ResponseEntity<?> addOrUpdateInventory(@RequestBody InventoryStockDto inventoryStockDto) {
        return inventoryService.addUpdateInventory(inventoryStockDto);
    }

    @GetMapping("/liveInventory")
    public ResponseEntity<?> getLiveInventory(){
        return inventoryService.getAllLiveInventory();
    }

    @PostMapping("/liveInventoryList")
    public ResponseEntity<?> getLiveInventoryList(@RequestBody FilterDto filterDto){
        return inventoryService.getLiveInventoryList(filterDto);
    }

    @PostMapping("/recordInventoryReturn")
    public ResponseEntity<?> recordInventoryExchange(@RequestBody InventoryExchangeDto inventoryExchangeDto){
        return inventoryService.recordInventoryExchange(inventoryExchangeDto);
    }

    @GetMapping("/getCustomerDetails/{customerId}")
    public ResponseEntity<?> getDetailsOfCustomer(@PathVariable Long customerId){
        return inventoryService.getDetailsOfCustomer(customerId);
    }

}