package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "srNo",
        "id",
        "point_holder_name",
        "mobile_number",
        "address",
        "point_name",
        "is_active"
})
public interface PointsResponseDto {

    @JsonProperty("srNo")
    Long getSrNo();

    @JsonProperty("id")
    Long getId();

    @JsonProperty("point_holder_name")
    String getPointHolderName();

    @JsonProperty("mobile_number")
    String getMobileNumber();

    @JsonProperty("address")
    String getAddress();

    @JsonProperty("point_name")
    String getPointName();

    @JsonProperty("is_active")
    Boolean getIsActive();

}
