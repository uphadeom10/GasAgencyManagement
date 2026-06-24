package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "serviceTypeId",
        "serviceName",
        "serviceRate",
        "description",
        "isActive"
})
public interface ServiceTypeByIdResponseDto {

    @JsonProperty("serviceTypeId")
    Long getServiceTypeId();
    @JsonProperty("serviceName")
    String getServiceName();
    @JsonProperty("serviceRate")
    Double getServiceRate();
    @JsonProperty("description")
    String getDescription();
    @JsonProperty("isActive")
    Boolean getIsActive();
}
