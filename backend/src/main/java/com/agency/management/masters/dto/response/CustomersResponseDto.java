package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "srNo",
        "Id",
        "customer_name",
        "mobile_number",
        "address",
        "is_active"
})
public interface CustomersResponseDto {

    @JsonProperty("srNo")
    Long getSrNo();

    @JsonProperty("Id")
    Long getId();

    @JsonProperty("customer_name")
    String getCustomerName();

    @JsonProperty("mobile_number")
    String getMobileNumber();

    @JsonProperty("address")
    String getAddress();

    @JsonProperty("is_active")
    Boolean getIsActive();
}
