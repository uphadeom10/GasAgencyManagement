package com.agency.management.masters.dto.response;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Sr_no",
        "id",
        "status",
        "isActive"
})
public interface StatusResponseList {

    @JsonProperty("Sr_no")
    Long getSrNo();

    @JsonProperty("id")
    Long getId();

    @JsonProperty("status")
    String getStatus();

    @JsonProperty("isActive")
    Boolean isActive = true;
}
