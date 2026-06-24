package com.agency.management.operations.manager.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AssignToDelivaryBoyDto {

    private Long assignmentId;

    private Long deliveryPersonId;

    private Long assignedById;

    private Long statusId;
}
