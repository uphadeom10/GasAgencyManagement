package com.agency.management.vehicles.dto.response;


import com.agency.management.vehicles.enums.FuelType;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.time.LocalDate;

@JsonPropertyOrder({
        "sr_no",
        "vehicle_id",
        "vehicle_number",
        "vehicle_type",
        "vehicle_model",
        "fuel_type",
        "load_capacity",
        "last_service_date",
        "next_service_due"
})
public interface GetvehicleDueForService {

    @JsonProperty("sr_no")
    Long getSrNo();

    @JsonProperty("vehicle_id")
    Long getVehicleId();

    @JsonProperty("vehicle_number")
    String getVehicleNumber();

    @JsonProperty("vehicle_type")
    String getVehicleType();

    @JsonProperty("vehicle_model")
    String getVehicleModel();

    @JsonProperty("fuel_type")
    FuelType getFuelType();

    @JsonProperty("load_capacity")
    Double getLoadCapacity();

    @JsonProperty("last_service_date")
    LocalDate getLastServiceDate();

    @JsonProperty("next_service_due")
    LocalDate getNextServiceDue();
}
