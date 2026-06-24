package com.agency.management.operations.deliveryperson.dto.request;

import com.agency.management.masters.entity.*;
import com.agency.management.operations.manager.entity.DailyAssignment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DailyDeliveryRequest {

    private Long id;

    @JsonProperty("deliveryPersonId")
    private Users deliveryPersonId;

    @JsonProperty("dailyAssignmentId")
    private DailyAssignment dailyAssignmentId;

    @JsonIgnore
    private Long createdBy;

    @JsonIgnore
    private Long lastModifiedBy;

    @JsonIgnore
    private Boolean isPoint;

    @JsonIgnore
    private Boolean isCustomer;

    @JsonIgnore
    private Customer customerId;

    @JsonIgnore
    private AgencyPoints agencyPointsId;

    @JsonProperty("isCash")
    private Boolean isCash;

    @JsonProperty("cashAmount")
    private Double cashAmount;

    @JsonProperty("isOnline")
    private Boolean isOnline;

    @JsonProperty("bankAccountId")
    private BankAccount bankAccountId;

    @JsonProperty("onlineAmount")
    private Double onlineAmount;

    @JsonIgnore
    private Status status;

    @JsonIgnore
    private Boolean isCompletedByDeliveryPerson;

    @JsonIgnore
    private Boolean isDelete = false;
}