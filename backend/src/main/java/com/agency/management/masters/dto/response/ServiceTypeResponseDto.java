package com.agency.management.masters.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "srNo",
        "Id",
        "service_name",
        "service_rate",
        "description",
        "is_active"
})
public interface ServiceTypeResponseDto {

    @JsonProperty("srNo")
    Long getSrNo();

    @JsonProperty("Id")
    Long getId();

    @JsonProperty("service_name")
    String getServiceName();

    @JsonProperty("service_rate")
    Double getServiceRate();

    @JsonProperty("description")
    String getDescription();

    @JsonProperty("is_active")
    Boolean getIsActive();
}
