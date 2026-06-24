package com.agency.management.masters.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "Id",
        "firstName",
        "lastName",
        "mobileNumber",
        "aadharCardNumber",
        "photoPath",
        "userName",
        "isActive",
        "rolee"
})
public interface UserByIdResponseDto {

    @JsonProperty("Id")
    Long getId();

    @JsonProperty("firstName")
    String getFirstName();

    @JsonProperty("lastName")
    String getLastName();

    @JsonProperty("mobileNumber")
    String getMobileNumber();


    @JsonProperty("aadharCardNumber")
    String getAadharCardNumber();

    @JsonProperty("photoPath")
    String getPhotoPath();

    @JsonProperty("userName")
    String getUserName();

    @JsonProperty("isActive")
    Boolean getIsActive();

    @JsonProperty("rolee")
    String getRolee();

}
