package com.agency.management.vehicles.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Setter
@Getter
public class VehicleServiceRecordRequestDto {

    private Long id;

    private Long vehicleId;

    private LocalDate serviceDate;

    private String description;

    private String servicedBy;

    private String location;

    private Integer odometerReading;

    private Double serviceCost;
}
