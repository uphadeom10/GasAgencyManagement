package com.agency.management.operations.manager.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DPdpDailyCloserConfirmationResponseDto {

    private Long daily_assignment_id;

    private Long assigned_by_id;

    private String assigned_by_name;

    private Long delivered_by_id;

    private String delivered_by_name;

    private String status;

    private Boolean isCompletedByDeliveryPerson;
}
