package com.agency.management.operations.deliveryperson.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CustomerDto {

    private Long id;

    private String customerName;

    private String mobileNumber;

    private String address;
}

