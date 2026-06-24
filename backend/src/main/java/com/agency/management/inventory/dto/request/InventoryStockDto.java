package com.agency.management.inventory.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InventoryStockDto {

    private Long id;

    private Long productCategoryId;

    private Long productId;

    private Integer totalQuantity;

    private Integer filled;

    private Integer unFilled;

    private Double unitPrice;

    private Boolean isAdded;

    private Boolean isRemoved;

    private String reason;

    private Boolean isNewConnection;

    private Long createdBy;

    private Long LastModifiedBy;
}
