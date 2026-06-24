package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "id",
        "customerName",
        "mobileNumber",
        "address",
        "isActive"

})
public interface CustomerByIdResponseDto {

    @JsonProperty("id")
    Long getId();
    @JsonProperty("customerName")
    String getCustomerName();
    @JsonProperty("mobileNumber")
    String getMobileNumber();
    @JsonProperty("address")
    String getAddress();
    @JsonProperty("isActive")
    Boolean getIsActive();

}
