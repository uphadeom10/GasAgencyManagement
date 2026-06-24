package com.agency.management.operations.admin.dto.request;

import com.agency.management.masters.entity.Status;
import com.agency.management.masters.entity.Users;
import com.agency.management.operations.manager.entity.DPDailyCloserConfirmation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EodConfirmationRequestDto {

    private DPDailyCloserConfirmation dailyClosureByManager_id;

    private Double totalCash;

    private Double totalOnline;

    private Double totalBalance;

    private Integer totalAssignedCylinder;

    private Integer totalSaleOfCylinder;

    private Double totalReturnTanks;

    private Status statusId;

    private Users managerId;

    private Users adminId;

    private Boolean isDelete = false;

    private  Long createdBy;

    private  Long lastModifiedBy;

}