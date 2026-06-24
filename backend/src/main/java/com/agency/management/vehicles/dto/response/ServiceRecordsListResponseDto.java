package com.agency.management.vehicles.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class ServiceRecordsListResponseDto {

    private Long service_id;

    private LocalDate serviceDate;

    private String description;

    private String servicedBy;

    private String location;

    private Integer odometerReading;

    private Double serviceCost;

}
