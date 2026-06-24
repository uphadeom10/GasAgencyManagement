package com.agency.management.masters.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Id",
        "Role",
        "Is Active"
})
public interface RoleResponseList {

    @JsonProperty("Id")
    Long getId();

    @JsonProperty("Role")
    String getRole();

    @JsonProperty("Is Active")
    Boolean getIsActive();
}
