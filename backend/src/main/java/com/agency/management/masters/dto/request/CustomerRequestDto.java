package com.agency.management.masters.dto.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequestDto {

    private Long id;

    private String customerName;

    private String mobileNumber;

    private String address;

    private Boolean isActive = true;

    private Long createdBy;

    private Long lastModifiedBy;
}
