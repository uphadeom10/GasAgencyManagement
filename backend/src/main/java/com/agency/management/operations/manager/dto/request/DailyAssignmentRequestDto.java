package com.agency.management.operations.manager.dto.request;

import com.agency.management.masters.entity.AgencyPoints;
import com.agency.management.masters.entity.Customer;
import com.agency.management.masters.entity.Users;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DailyAssignmentRequestDto {

    private int dailyAssignmentId;

    private Users assignedById;

    private Boolean isCustomer;

    private Customer customerId;

    private Boolean isPoint;

    private AgencyPoints agencyPointId;

    //private Users deliveryPersonId;

    List<DailyAssignmentDetailsRequestDto> dailyAssignmentDetailsRequestDtos;

    private Boolean isCompletedByDeliveryPerson;

    private Long createdBy;

    private Long lastModifiedBy ;

}