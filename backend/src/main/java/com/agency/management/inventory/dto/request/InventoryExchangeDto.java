package com.agency.management.inventory.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class InventoryExchangeDto {

    private Long productId;
    private Long productCategoryId;
    private Long deliveryBoyId;
    private LocalDate exchangeDate;
    private Integer totalDelivered;
    private Integer totalExpectedReturn;
    private Integer filledReturned;
    private Integer unfilledReturned;
    private String remarks;
    private Long createdBy;
    private Long lastModifiedBy;
}


