package com.agency.management.operations.manager.dto.request;

import com.agency.management.masters.entity.Status;
import com.agency.management.masters.entity.Users;
import com.agency.management.operations.manager.entity.DailyAssignment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DPDailyCloserConfirmationRequestDto {

    private DailyAssignment dailyAssignmentId;

    @JsonIgnore
    private Status statusId;

    @JsonIgnore
    private Users confirmedById;

    @JsonIgnore
    private Users deliveredById;

    @JsonIgnore
    private Long createdBy;

    @JsonIgnore
    private Long lastModifiedBy;
}
