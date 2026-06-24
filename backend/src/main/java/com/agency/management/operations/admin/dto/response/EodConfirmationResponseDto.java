package com.agency.management.operations.admin.dto.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EodConfirmationResponseDto {

    private Long eodConfirmationId;

    private Double totalReceivedAmount;

    private Long adminId;

    private String admin_name;

    private Long managerId;

    private String manager_name;

    private Long statusId;

}
