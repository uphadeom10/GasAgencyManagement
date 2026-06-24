package com.agency.management.vehicles.controller;

import com.agency.management.common.FilterDto;
import com.agency.management.vehicles.dto.request.VehicleRequestDto;
import com.agency.management.vehicles.dto.request.VehicleServiceRecordRequestDto;
import com.agency.management.vehicles.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @PostMapping("/updateAndRegister")
    public ResponseEntity<?> RegisterOrUpdateVehicle(@RequestBody VehicleRequestDto vehicleRequestDto){
        return vehicleService.RegisterOrUpdateVehicle(vehicleRequestDto);
    }

    @GetMapping("/getVehicleOfUser/{userId}")
    public ResponseEntity<?> getVehicleAssignedToDeliveryBoy(@PathVariable Long userId){
        return vehicleService.getVehicleAssignedToDeliveryBoy(userId);
    }

    @GetMapping("/getVehicleById/{vehicleId}")
    public ResponseEntity<?> getVehicleDetailsById(@PathVariable Long vehicleId){
        return vehicleService.getVehicleDetailsById(vehicleId);
    }

    @PostMapping("/getAllVehiclesList")
    public ResponseEntity<?> getAllVehiclesList(@RequestBody FilterDto filterDto){
        return vehicleService.getAllVehicles(filterDto);
    }

    @PostMapping("/get_all_available_vehicles")
    public ResponseEntity<?> getAllAvailableVehiclesList(@RequestBody FilterDto filterDto){
        return vehicleService.getAllAvailbaleVehicles(filterDto);
    }

    @DeleteMapping("deleteVehicleById/{VehicleId}")
    public ResponseEntity<?> deleteVehicleById(@PathVariable Long VehicleId){
        return vehicleService.deleteVehicleById(VehicleId);
    }

    @GetMapping("/getVehiclesDueForService")
    public ResponseEntity<?> getVehiclesDueForServiceList(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate){
        return vehicleService.getVehiclesDueForService(fromDate, toDate);
    }

    @PostMapping("/addOrUpdateServicingDetails")
    public ResponseEntity<?> addOrUpdateVehicleServiceDetails(@RequestBody VehicleServiceRecordRequestDto vehicleServiceRecordRequestDto){
        return vehicleService.addOrUpdateServiceRecord(vehicleServiceRecordRequestDto);
    }

    @GetMapping("/getServiceDetailsOfVehicle/{vehicleId}")
    ResponseEntity<?> getServiceRecordsOfVehicleById(@PathVariable Long vehicleId){
        return vehicleService.getServiceRecordsOfVehicleById(vehicleId);
    }

    @GetMapping("/assignVehicleToUser")
    ResponseEntity<?> assignedVehicleToUser(@RequestParam Long userId, @RequestParam Long vehicleId){
        return vehicleService.assignedVehicleToUser(userId, vehicleId);
    }


}