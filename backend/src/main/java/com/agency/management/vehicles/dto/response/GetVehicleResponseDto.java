package com.agency.management.vehicles.dto.response;


import com.agency.management.vehicles.enums.FuelType;
import lombok.*;

import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GetVehicleResponseDto {

    private Long user_id;

    private String username;

    private String user_role;

    private String vehicleNumber;

    private String vehicleType;

    private String vehicleModel;

    private FuelType fuelType;

    private Double loadCapacity;

    private LocalDate lastServiceDate;

    private LocalDate nextServiceDue;
}
