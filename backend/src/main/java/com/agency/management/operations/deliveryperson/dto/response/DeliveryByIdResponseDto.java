package com.agency.management.operations.deliveryperson.dto.response;

import com.agency.management.masters.entity.Status;
import com.agency.management.masters.entity.Users;
import com.agency.management.operations.deliveryperson.dto.request.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class DeliveryByIdResponseDto {

    private Long id;
    private DailyAssignmentDto dailyAssignment;
    private Boolean isPoint;
    private Boolean isCustomer;
    private CustomerDto customer;
    private AgencyPointsDto agencyPoint;
    List<DailyDeliveryProductsRequestDto> dailyDeliveryProductsRequestDtoList;
    private Boolean isCash;
    private Double cashAmount;
    private Boolean isOnline;
    private BankAccountDto bankAccount;
    private Double onlineAmount;
    private Boolean isBalance;
    private Double balanceAmount;
    private StatusDto status;
}

