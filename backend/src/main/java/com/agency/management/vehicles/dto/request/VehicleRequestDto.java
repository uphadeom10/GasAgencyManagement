package com.agency.management.vehicles.dto.request;

import com.agency.management.masters.entity.Users;
import com.agency.management.vehicles.enums.FuelType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class VehicleRequestDto {

    private Long id;

    private String vehicleNumber;

    private String vehicleType;

    private String vehicleModel;

    private FuelType fuelType;

    private Double loadCapacity;

    private LocalDate lastServiceDate;

    private LocalDate nextServiceDue;

    private Users assignedTo;

    private Boolean isAdded;

    private Boolean isRemoved;

    private Long createdBy;

    private Long lastModifiedBy ;
}