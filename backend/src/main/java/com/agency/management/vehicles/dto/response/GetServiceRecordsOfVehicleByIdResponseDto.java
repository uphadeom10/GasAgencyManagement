package com.agency.management.vehicles.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class GetServiceRecordsOfVehicleByIdResponseDto {

    private Long vehicle_owner_id;

    private String vehicle_Owner_name;

    private Long vehicleId;

    private String vehicleNumber;

    private String vehicleType;

    private String vehicleModel;

    private String fuelType;

    private LocalDate dueDateAtTimeOfService;

    private Boolean isServicedOnDueDate;

    private List<ServiceRecordsListResponseDto> serviceRecordsListResponseDto;

}
