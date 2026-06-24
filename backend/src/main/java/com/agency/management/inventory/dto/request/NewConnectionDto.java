package com.agency.management.inventory.dto.request;

import com.agency.management.masters.entity.BankAccount;
import com.agency.management.masters.entity.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NewConnectionDto {

    private Long id;

    private Customer customerId;

    private Boolean isNewConnection;

    private Boolean isDBC;

    private Boolean isInventoryBuy;

    private Boolean isCash;

    private Double cashAmount = 0.00;

    private Boolean isOnline;

    private BankAccount bankAccountId;

    private Double onlineAmount = 0.00;

    private String onlinePhotoPath;

    private List<NewConnectionDetailsDto> newConnectionDetailsDtoList;

    private Long createdBy;

    private Long lastModifiedBy ;

}
