package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({

        "srno",
        "id",
        "pointHolderName",
        "mobileNumber",
        "address",
        "pointName",
        "isActive"
})
public interface AgencyPointResponseList {

    @JsonProperty("srno")
    Long getSrNo();
    @JsonProperty("id")
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
    Boolean getIsActive();
}
