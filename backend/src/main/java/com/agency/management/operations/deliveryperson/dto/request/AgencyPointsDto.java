package com.agency.management.operations.deliveryperson.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AgencyPointsDto {
    private Long id;
    private String pointHolderName;
    private String mobileNumber;
    private String address;
    private String pointName;
}
