package com.agency.management.vehicles.service;

import com.agency.management.common.FilterDto;
import com.agency.management.vehicles.dto.request.VehicleRequestDto;
import com.agency.management.vehicles.dto.request.VehicleServiceRecordRequestDto;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public interface VehicleService {

    ResponseEntity<?> RegisterOrUpdateVehicle(VehicleRequestDto VehicleRequestDto);

    ResponseEntity<?> getVehicleAssignedToDeliveryBoy(Long userId);

    ResponseEntity<?> getVehicleDetailsById(Long vehicleId);

    ResponseEntity<?> getAllVehicles(FilterDto filterDto);

    ResponseEntity<?> getAllAvailbaleVehicles(FilterDto filterDto);

    ResponseEntity<?> deleteVehicleById(Long vehicleId);

    ResponseEntity<?> getVehiclesDueForService(Date fromDate, Date toDate);

    ResponseEntity<?> addOrUpdateServiceRecord(VehicleServiceRecordRequestDto vehicleServiceRecordRequestDto);

    ResponseEntity<?> getServiceRecordsOfVehicleById(Long vehicleId);

    ResponseEntity<?> assignedVehicleToUser(Long userId, Long vehicleId);


}