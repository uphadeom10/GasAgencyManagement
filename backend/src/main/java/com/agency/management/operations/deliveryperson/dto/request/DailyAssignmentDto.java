package com.agency.management.operations.deliveryperson.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DailyAssignmentDto {

    private Long id;
    private UserDto deliveryPerson;
    private UserDto assignBy;
    private Boolean isCompletedByDeliveryPerson;
}
