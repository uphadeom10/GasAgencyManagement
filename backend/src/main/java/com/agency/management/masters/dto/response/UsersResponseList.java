package com.agency.management.masters.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Sr_no",
        "Id",
        "Employee_name",
        "Mobile_number",
        "UserName",
        "Status",
        "Role"
})
public interface UsersResponseList {
    @JsonProperty("Sr_no")
    Long getSrNo();

    @JsonProperty("Id")
    Long getId();

    @JsonProperty("Employee_name")
    String getNamee();

    @JsonProperty("Mobile_number")
    String getMobileNumber();

    @JsonProperty("UserName")
    String getUserName();

    @JsonProperty("Status")
    Boolean getIsActive();

    @JsonProperty("Role")
    String getRolee();
}
