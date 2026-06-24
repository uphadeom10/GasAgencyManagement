package com.agency.management.operations.deliveryperson.dto.response;

import com.agency.management.masters.entity.Users;
import com.agency.management.operations.deliveryperson.dto.request.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AssignmentsOfDeliveryBoyResponseDto {

    private Long assignmentId;

    private Boolean isPoint;

    private Boolean isCustomer;

    private Long customerId;

    private String CustomerName;

    private Long agencyPointId;

    private String agencyPointName;

    private String address;

    List<ProductsOfAssignmentResponseDto> productsOfAssignmentResponseDtoList;

    private Boolean isCompletedByDeliveryPerson;

    private Long statusId;

    private String status;
}
