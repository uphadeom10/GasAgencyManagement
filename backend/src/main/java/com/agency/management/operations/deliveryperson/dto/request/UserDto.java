package com.agency.management.operations.deliveryperson.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String mobileNumber;
}
