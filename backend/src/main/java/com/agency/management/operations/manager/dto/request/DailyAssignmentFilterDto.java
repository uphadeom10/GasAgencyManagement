package com.agency.management.operations.manager.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DailyAssignmentFilterDto {

    Long deliveryPersonId;
    Long assignedById;
    Integer page;
    Integer size;

}
