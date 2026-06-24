package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "ID",
        "pointHolderName",
        "mobileNumber",
        "address",
        "pointName",
        "isActive"})
public interface AgencyPointByIdResponseDto {

    @JsonProperty("Id")
    Long getId();

    @JsonProperty("pointHolderName")
    String getPointHolderName();

    @JsonProperty("mobileNumber")
    String getMobileNumber();

    @JsonProperty("address")
    String getAddress();

    @JsonProperty("pointName")
    String getPointName();

    @JsonProperty("isActive")
    String getIsActive();


}
