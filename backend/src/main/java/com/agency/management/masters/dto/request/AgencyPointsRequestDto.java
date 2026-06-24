package com.agency.management.masters.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgencyPointsRequestDto {

    private Long id;

    @NotNull(message = "pointHolderName cannot be null")
    private String pointHolderName;

    @NotNull(message = "mobileNumber cannot be null")
    private String mobileNumber;

    private String address;

    @NotNull(message = "pointName cannot be null")
    private String pointName;

    private Boolean isActive = true;

    private Long createdBy;

    private Long lastModifiedBy;
}
