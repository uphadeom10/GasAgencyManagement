
package com.agency.management.vehicles.service.impl;

import com.agency.management.common.ApiResponse;
import com.agency.management.common.FilterDto;
import com.agency.management.login.dto.request.LoginRequestDto;
import com.agency.management.login.service.impl.LoginServiceImpl;
import com.agency.management.masters.entity.Users;
import com.agency.management.masters.repository.UserRepository;
import com.agency.management.vehicles.dto.request.VehicleRequestDto;
import com.agency.management.vehicles.dto.request.VehicleServiceRecordRequestDto;
import com.agency.management.vehicles.dto.response.*;
import com.agency.management.vehicles.entity.Vehicle;
import com.agency.management.vehicles.entity.VehicleServiceRecord;
import com.agency.management.vehicles.repositoy.VehicleRepository;
import com.agency.management.vehicles.repositoy.VehicleServiceRecordRepository;
import com.agency.management.vehicles.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class VehicleServiceImpl implements VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleServiceRecordRepository vehicleServiceRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LoginServiceImpl loginServiceImpl;

    @Override
    public ResponseEntity<?> RegisterOrUpdateVehicle(VehicleRequestDto vehicleRequestDto) {
        var response = new ApiResponse<>();

        Vehicle vehicle = new Vehicle();

        if (vehicleRequestDto.getId() == null) {

            vehicle.setVehicleNumber(vehicleRequestDto.getVehicleNumber());
            vehicle.setVehicleType(vehicleRequestDto.getVehicleType());
            vehicle.setVehicleModel(vehicleRequestDto.getVehicleModel());
            vehicle.setFuelType(vehicleRequestDto.getFuelType());
            vehicle.setLoadCapacity(vehicleRequestDto.getLoadCapacity());
            vehicle.setLastServiceDate(vehicleRequestDto.getLastServiceDate());
            if (vehicleRequestDto.getLastServiceDate() != null) {
                vehicle.setNextServiceDue(vehicleRequestDto.getLastServiceDate().plusMonths(3));
            }
            vehicle.setAssignedTo(vehicleRequestDto.getAssignedTo());
            vehicle.setIsAdded(true);
            vehicle.setIsRemoved(false);
            vehicle.setCreatedBy(vehicleRequestDto.getCreatedBy());
            vehicle.setLastModifiedBy(vehicleRequestDto.getLastModifiedBy());

            response.responseMethod(HttpStatus.CREATED.value(), "VEHICLE REGISTERED SUCCESSFULLY!!!", vehicle, null);

        } else {

            vehicle = vehicleRepository.findById(vehicleRequestDto.getId()).orElseThrow(() ->
                    new RuntimeException("Vehicle not found with ID: " + vehicleRequestDto.getId())
            );

            vehicle.setVehicleNumber(vehicleRequestDto.getVehicleNumber());
            vehicle.setVehicleType(vehicleRequestDto.getVehicleType());
            vehicle.setVehicleModel(vehicleRequestDto.getVehicleModel());
            vehicle.setFuelType(vehicleRequestDto.getFuelType());
            vehicle.setLoadCapacity(vehicleRequestDto.getLoadCapacity());
            vehicle.setLastServiceDate(vehicleRequestDto.getLastServiceDate());
            if (vehicleRequestDto.getLastServiceDate() != null) {
                vehicle.setNextServiceDue(vehicleRequestDto.getLastServiceDate().plusMonths(3));
            }
            vehicle.setAssignedTo(vehicleRequestDto.getAssignedTo());
            vehicle.setIsAdded(true);
            vehicle.setIsRemoved(false);

            vehicle.setCreatedBy(vehicleRequestDto.getCreatedBy());
            vehicle.setLastModifiedBy(vehicleRequestDto.getLastModifiedBy());

            response.responseMethod(HttpStatus.OK.value(), "VEHICLE DETAILS UPDATE SUCCESSFULLY", null, null);
        }
        vehicleRepository.save(vehicle);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getVehicleAssignedToDeliveryBoy(Long userId) {
        var response = new ApiResponse<>();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND WITH ID: " + userId));

        List<GetVehicleResponseDto> vehicles = vehicleRepository.findVehicleDetailsByAssignedToId(userId);

        if (vehicles == null || vehicles.isEmpty()) {
            throw new RuntimeException("No vehicle assigned to user ID: " + userId);
        }

        response.responseMethod(HttpStatus.OK.value(), "Vehicles fetched successfully", vehicles, null);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getVehicleDetailsById(Long vehicleId) {
        var response = new ApiResponse<>();

        Optional<Vehicle> vehicleById = vehicleRepository.findById(vehicleId);

        if (vehicleById.isPresent()) {
            Vehicle vehicle = vehicleById.get();

            Users assignedUser = vehicle.getAssignedTo();
            if (assignedUser == null) {
                response.responseMethod(HttpStatus.NOT_FOUND.value(), "No user assigned to vehicle with ID: " + vehicleId, null, null);
                return ResponseEntity.ok(response);
            }

            Optional<Users> user = userRepository.findById(assignedUser.getId());
            if (user.isEmpty()) {
                response.responseMethod(HttpStatus.NOT_FOUND.value(), "Assigned user not found for vehicle ID: " + vehicleId, null, null);
                return ResponseEntity.ok(response);
            }

            GetServiceRecordsOfVehicleByIdResponseDto details = new GetServiceRecordsOfVehicleByIdResponseDto();

            details.setVehicle_owner_id(user.get().getId());
            details.setVehicle_Owner_name(user.get().getFirstName() + " " + user.get().getLastName());
            details.setVehicleId(vehicle.getId());
            details.setVehicleNumber(vehicle.getVehicleNumber());
            details.setVehicleType(vehicle.getVehicleType());
            details.setVehicleModel(vehicle.getVehicleModel());
            details.setFuelType(vehicle.getFuelType().toString());
            details.setDueDateAtTimeOfService(vehicle.getNextServiceDue());
            details.setIsServicedOnDueDate(LocalDate.now().isEqual(vehicle.getNextServiceDue()));

            List<VehicleServiceRecord> serviceRecords = vehicleServiceRecordRepository.findByVehicleId(vehicle.getId());

            if (serviceRecords == null || serviceRecords.isEmpty()) {
                response.responseMethod(HttpStatus.OK.value(), "NO SERVICE RECORDS FOUND FOR VEHICLE WITH ID : " + vehicle.getId(), details, null);
            } else {
                List<ServiceRecordsListResponseDto> recordsList = new ArrayList<>();
                for (VehicleServiceRecord record : serviceRecords) {
                    ServiceRecordsListResponseDto dto = new ServiceRecordsListResponseDto();
                    dto.setService_id(record.getId());
                    dto.setServiceDate(record.getServiceDate());
                    dto.setDescription(record.getDescription());
                    dto.setServicedBy(record.getServicedBy());
                    dto.setLocation(record.getLocation());
                    dto.setOdometerReading(record.getOdometerReading());
                    dto.setServiceCost(record.getServiceCost());
                    recordsList.add(dto);
                }
                details.setServiceRecordsListResponseDto(recordsList);
                response.responseMethod(HttpStatus.OK.value(), "VEHICLE DETAILS FOUND", details, null);
            }
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "VEHICLE DETAILS NOT FOUND WITH ID: " + vehicleId, null, null);
        }

        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<?> getAllVehicles(FilterDto filterDto) {

        var response = new ApiResponse<>();

        List<GetVehiclesListResponseDto> list = vehicleRepository.getAllvehiclesList(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());

        if (list.isEmpty() || list == null)
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "VEHICLES NOT FOUND", null, null);
        else
            response.responseMethod(HttpStatus.OK.value(), "VEHICLES FOUND", list, null);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> getAllAvailbaleVehicles(FilterDto filterDto) {
        var response = new ApiResponse<>();

        List<GetVehiclesListResponseDto> list = vehicleRepository.getAllAvailableVehicles(filterDto.getId(), filterDto.getSearchString(), filterDto.getPage(), filterDto.getSize());

        if (list.isEmpty() || list == null)
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "NO VEHICLES ARE AVAILABLE", null, null);
        else
            response.responseMethod(HttpStatus.OK.value(), "AVAILABLE VEHICLES FOUND", list, null);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteVehicleById(Long vehicleId) {
        var response = new ApiResponse<>();

        Optional<Vehicle> optionalVehicle = vehicleRepository.findById(vehicleId);

        if (optionalVehicle.isPresent()) {
            Vehicle vehicle = optionalVehicle.get();
            if (Boolean.TRUE.equals(vehicle.getIsAdded())) {
                vehicle.setIsRemoved(true);
                vehicle.setIsAdded(false);
                vehicleRepository.save(vehicle);
                response.responseMethod(HttpStatus.OK.value(), "VEHICLE WITH ID : " + vehicleId + " DELETED SUCESSFULLY", null, null);
            } else {
                response.responseMethod(HttpStatus.NOT_FOUND.value(), "VEHICLE WITH ID : " + vehicleId + " IS ALREADY REMOVED OR NOT ACTIVE", null, null);
            }
        } else {
            response.responseMethod(HttpStatus.NOT_FOUND.value(), "VEHICLE WITH ID : " + vehicleId + " NOT FOUND", null, null);
        }

        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<?> getVehiclesDueForService(Date fromDate, Date toDate) {
        var response = new ApiResponse<>();

        List<GetvehicleDueForService> list = vehicleRepository.getVehiclesDueForService(fromDate, toDate);

        if (list != null) {
            response.responseMethod(HttpStatus.OK.value(), "BELLOW VEHICLES REQUIRED SERVICING AS SOON AS POSSIBLE", list, null);
        } else {
            response.responseMethod(HttpStatus.OK.value(), "NO VEHICLES FOUND FOR SERVICE", null, null);
        }

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> addOrUpdateServiceRecord(VehicleServiceRecordRequestDto dto) {
        var response = new ApiResponse<>();
        VehicleServiceRecord record;

        if (dto.getId() == null) {
            record = new VehicleServiceRecord();
            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("VEHICLE NOT FOUND"));

            record.setVehicle(vehicle);
            record.setServiceDate(dto.getServiceDate());
            record.setDescription(dto.getDescription());
            record.setServicedBy(dto.getServicedBy());
            record.setLocation(dto.getLocation());
            record.setOdometerReading(dto.getOdometerReading());
            record.setServiceCost(dto.getServiceCost());

            LocalDate dueDate = vehicle.getNextServiceDue();
            record.setDueDateAtTimeOfService(dueDate);
            record.setIsServicedOnDueDate(dto.getServiceDate() != null && dueDate != null &&
                    dto.getServiceDate().isEqual(dueDate));

            vehicleServiceRecordRepository.save(record);

            Optional<Vehicle> vehicleById = vehicleRepository.findById(vehicle.getId());
            vehicleById.get().setLastServiceDate(dto.getServiceDate());
            vehicleById.get().setNextServiceDue(dto.getServiceDate().plusMonths(3));
            vehicleRepository.save(vehicleById.get());

            response.responseMethod(HttpStatus.CREATED.value(), "VEHICLE SERVICING DONE", null, null);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            record = vehicleServiceRecordRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("SERVICE RECORD NOT FOUND WITH ID : " + dto.getId()));

            Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> new RuntimeException("VEHICLE NOT FOUND FOR SERVICING"));

            record.setVehicle(vehicle);
            record.setServiceDate(dto.getServiceDate());
            record.setDescription(dto.getDescription());
            record.setServicedBy(dto.getServicedBy());
            record.setLocation(dto.getLocation());
            record.setOdometerReading(dto.getOdometerReading());
            record.setServiceCost(dto.getServiceCost());

            LocalDate dueDate = vehicle.getNextServiceDue();
            record.setDueDateAtTimeOfService(dueDate);
            record.setIsServicedOnDueDate(dto.getServiceDate() != null && dueDate != null &&
                    dto.getServiceDate().isEqual(dueDate));

            vehicleServiceRecordRepository.save(record);

            Optional<Vehicle> vehicleById = vehicleRepository.findById(vehicle.getId());
            vehicleById.get().setLastServiceDate(dto.getServiceDate());
            vehicleById.get().setNextServiceDue(vehicleById.get().getNextServiceDue().plusMonths(3));
            vehicleRepository.save(vehicleById.get());

            response.responseMethod(HttpStatus.OK.value(), "VEHICLE SERVICE UPDATED", null, null);
            return ResponseEntity.ok(response);
        }
    }

    @Override
    public ResponseEntity<?> getServiceRecordsOfVehicleById(Long vehicleId) {
        var response = new ApiResponse<>();

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("VEHICLE NOT FOUND WITH ID : " + vehicleId));

        Users user = userRepository.findById(vehicle.getAssignedTo().getId())
                .orElseThrow(() -> new RuntimeException("MANAGER OR DELIVERY BOY NOT FOUND WITH ID :" + vehicle.getAssignedTo().getId()));

        GetServiceRecordsOfVehicleByIdResponseDto details = new GetServiceRecordsOfVehicleByIdResponseDto();

        details.setVehicle_owner_id(user.getId());
        details.setVehicle_Owner_name(user.getFirstName() + " " + user.getLastName());
        details.setVehicleId(vehicle.getId());
        details.setVehicleNumber(vehicle.getVehicleNumber());
        details.setVehicleType(vehicle.getVehicleType());
        details.setVehicleModel(vehicle.getVehicleModel());
        details.setFuelType(vehicle.getFuelType().toString());
        details.setDueDateAtTimeOfService(vehicle.getNextServiceDue());
        details.setIsServicedOnDueDate(LocalDate.now().isEqual(vehicle.getNextServiceDue()));

        List<VehicleServiceRecord> serviceRecords = vehicleServiceRecordRepository.findByVehicleId(vehicle.getId());

        if (serviceRecords == null || serviceRecords.isEmpty()) {
            response.responseMethod(HttpStatus.OK.value(), "NO SERVICE RECORDS FOUND FOR VEHICLE WITH ID : " + vehicle.getId(), details, null);
        } else {
            List<ServiceRecordsListResponseDto> recordsList = new ArrayList<>();

            for (VehicleServiceRecord record : serviceRecords) {
                ServiceRecordsListResponseDto dto = new ServiceRecordsListResponseDto();
                dto.setService_id(record.getId());
                dto.setServiceDate(record.getServiceDate());
                dto.setDescription(record.getDescription());
                dto.setServicedBy(record.getServicedBy());
                dto.setLocation(record.getLocation());
                dto.setOdometerReading(record.getOdometerReading());
                dto.setServiceCost(record.getServiceCost());
                recordsList.add(dto);
            }
            details.setServiceRecordsListResponseDto(recordsList);
        }

        response.responseMethod(HttpStatus.OK.value(), "SERVICE RECORDS FETCHED SUCCESSFULLY", details, null);

        return ResponseEntity.ok(response);
    }


    @Override
    public ResponseEntity<?> assignedVehicleToUser(Long userId, Long vehicleId) {

        var response = new ApiResponse<>();

        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND WITH ID : " + userId));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("VEHICLE NOT FOUND WITH ID :" + vehicleId));

        vehicle.setAssignedTo(user);
        vehicleRepository.save(vehicle);

        response.responseMethod(HttpStatus.OK.value(), "VEHICLE '" + vehicle.getVehicleModel() + "' WITH ID : " + vehicleId + " ASSIGNED TO USER '" + user.getFirstName() + "' WITH ID : " + userId, null, null);

        return ResponseEntity.ok(response);
    }


}
