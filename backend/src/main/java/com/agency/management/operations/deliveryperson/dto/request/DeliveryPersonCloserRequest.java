package com.agency.management.operations.deliveryperson.dto.request;
import com.agency.management.operations.manager.entity.DailyAssignment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DeliveryPersonCloserRequest {

    private Long id;

    private DailyAssignment dailyAssignmentId;

    @JsonIgnore
    private Double totalCash;

    @JsonIgnore
    private Double totalOnline;

    @JsonIgnore
    private Double totalBalance;

    @JsonIgnore
    private Integer totalAssignedCylinder;

    @JsonIgnore
    private Integer totalSaleOfCylinder;

    @JsonIgnore
    private Integer totalReturnTanks;

    @JsonIgnore
    private  Long createdBy;

    @JsonIgnore
    private  Long lastModifiedBy;

}
